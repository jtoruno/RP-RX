package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface PaymentSelectionVM {
    interface Inputs {
        fun paymentMethodChanged(paymentMethod: PaymentMethod)
        fun nextButtonPressed()
        fun applyRewardsRowPressed(state : Boolean)
        fun descriptionTextFieldChanged(description: String)
    }
    interface Outputs {
        fun paymentMethodChangedAction() : Observable<PaymentMethod>
        fun showError() : Observable<String>

        /// Emits when the payment was processed.
        fun finishPaymentProcessingAction() : Observable<Transaction>

        /// Emits when the next button loading indicator changes.
        fun nextButtonLoadingIndicator() : Observable<Boolean>

        /// Emits when the payment method is changed.
        fun paymentInformationChangedAction() : Observable<PaymentInformation>

        fun showVendor() : Observable<Vendor>

        fun applyRewards() : Observable<Boolean>

    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<PaymentSelectionVM>(environment), Inputs, Outputs{


        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val paymentMethodChanged = PublishSubject.create<PaymentMethod>()
        private val nextButtonPressed = PublishSubject.create<Unit>()
        private val applyRewardsRowPressed = BehaviorSubject.createDefault(false)
        private val descriptionTextFieldChanged = BehaviorSubject.createDefault("")

        //Outputs
        private val paymentMethodChangedAction = BehaviorSubject.create<PaymentMethod>()
        private val showError = BehaviorSubject.create<String>()
        private val finishPaymentProcessingAction = BehaviorSubject.create<Transaction>()
        private val nextButtonLoadingIndicator = BehaviorSubject.create<Boolean>()
        private val paymentInformationChangedAction = BehaviorSubject.create<PaymentInformation>()
        private val applyRewards = BehaviorSubject.create<Boolean>()

        private val showVendor = BehaviorSubject.create<Vendor>()
        private val amountObserver = BehaviorSubject.create<Float>()
        private val orderObservable = BehaviorSubject.create<Order>()
        private val rediPointsObservable = BehaviorSubject.create<Double>()

        private lateinit var paymentPayload: PaymentPayload
        private var amount : Float = 0F

        init {
            val payloadIntent = intent()
                .filter { it.hasExtra("SPSelectionObject") }
                .map {
                    val result = it.getSerializableExtra("SPSelectionObject") as SitePaySellerSelectionObject
                    paymentPayload = result.payload
                    return@map result
                }

            payloadIntent
                .map { return@map it.vendor }
                .subscribe(this.showVendor)

            payloadIntent
                .map { return@map it.payload.order }
                .subscribe(this.orderObservable)

            payloadIntent
                .map { return@map it.payload.rediPuntos }
                .subscribe(this.rediPointsObservable)

            val amountIntent = intent()
                .filter { it.hasExtra("amount") }
                .map {
                    return@map it.getFloatExtra("amount",0F)
                }

            amountIntent
                .subscribe(this.amountObserver)




            this.paymentMethodChanged
                .subscribe(this.paymentMethodChangedAction)

            this.paymentMethodChanged
                .map { return@map PaymentInformation(paymentPayload.rediPuntos,it.rewards, amount.toDouble(),paymentPayload.order.fee,paymentPayload.order.tax,paymentPayload.order.total) }
                .subscribe(this.paymentInformationChangedAction)
            /*
            val form = Observable.combineLatest<PaymentMethod,PaymentInformation,Pair<PaymentMethod,PaymentInformation>>(this.paymentMethodChanged,this.paymentInformationChangedAction,
                BiFunction { t1, t2 ->
                Pair(t1, t2)
            })*/

            val form = Observables.combineLatest(this.paymentMethodChanged,this.paymentInformationChangedAction,this.descriptionTextFieldChanged)

            val requestPaymentEvent = form
                .takeWhen(this.nextButtonPressed)
                .flatMap {
                    var wayToPayInput : WayToPayInput
                    println("Hello"+applyRewardsRowPressed.value)
                    wayToPayInput = if(this.applyRewardsRowPressed.value){
                        WayToPayInput(it.second.second.usedRediPoints,it.second.first.cardId,0.0,it.second.second.cardAmountToPay)

                    }else{
                        WayToPayInput(0.0,it.second.first.cardId,0.0,it.second.second.total)
                    }
                    val description = if (it.second.third.isNullOrEmpty()){
                        null
                    }else{
                        it.second.third
                    }
                    val requestPaymentInput = RequestPaymentInput(environment.currentUser().getCurrentUser()?.userId?:"",this.paymentPayload.order.pid,wayToPayInput, description)
                    return@flatMap this.requestPayment(requestPaymentInput)
                }
                .share()

            requestPaymentEvent
                .filter { it.isFail() }
                .map { return@map "Ocurrió un error al procesar el pago. Por favor intente de nuevo." }
                .subscribe(this.showError)

            requestPaymentEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.finishPaymentProcessingAction)

            applyRewardsRowPressed
                .subscribe(this.applyRewards)
        }

        override fun applyRewardsRowPressed(state: Boolean) {
            return this.applyRewardsRowPressed.onNext(state)
        }

        override fun applyRewards(): Observable<Boolean> = this.applyRewards


        override fun paymentMethodChanged(paymentMethod: PaymentMethod) {
            return this.paymentMethodChanged.onNext(paymentMethod)
        }

        override fun nextButtonPressed() {
            return this.nextButtonPressed.onNext(Unit)
        }

        override fun descriptionTextFieldChanged(description: String) {
            return this.descriptionTextFieldChanged.onNext(description)
        }

        override fun paymentMethodChangedAction(): Observable<PaymentMethod> = this.paymentMethodChangedAction

        override fun showError(): Observable<String> = this.showError

        override fun finishPaymentProcessingAction(): Observable<Transaction> = this.finishPaymentProcessingAction

        override fun nextButtonLoadingIndicator(): Observable<Boolean> = this.nextButtonLoadingIndicator

        override fun paymentInformationChangedAction(): Observable<PaymentInformation> = this.paymentInformationChangedAction

        override fun showVendor(): Observable<Vendor> = this.showVendor

        private fun requestPayment(input : RequestPaymentInput) : Observable<Result<Transaction>>{
            return environment.userUseCase().requestPayment(input)
                .doOnComplete { this.nextButtonLoadingIndicator.onNext(false) }
                .doOnSubscribe { this.nextButtonLoadingIndicator.onNext(true) }
        }



        fun getPaymentMethods() : List<PaymentMethod>{
            return this.paymentPayload.paymentMethods
        }

        fun getVendor() : Vendor {
            return this.showVendor.value
        }

        fun getAmount() : Float {
            return this.amountObserver.value
        }

        fun getOrder() : Order {
            return this.orderObservable.value
        }

        fun rediPoints() : Double{
            return this.rediPointsObservable.value
        }

    }
}