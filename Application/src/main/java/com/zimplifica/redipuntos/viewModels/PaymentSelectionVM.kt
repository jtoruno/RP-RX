package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface PaymentSelectionVM {
    interface Inputs {
        fun paymentMethodChanged(paymentMethod: PaymentMethod)
        fun nextButtonPressed()
    }
    interface Outputs {
        fun paymentMethodChangedAction() : Observable<PaymentMethod>
        fun showError() : Observable<String?>

        /// Emits when the payment was processed.
        fun finishPaymentProcessingAction() : Observable<Unit>

        /// Emits when the next button loading indicator changes.
        fun nextButtonLoadingIndicator() : Observable<Boolean>

        /// Emits when the payment method is changed.
        fun paymentInformationChangedAction() : Observable<PaymentInformation>

    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<PaymentSelectionVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val paymentMethodChanged = PublishSubject.create<PaymentMethod>()
        private val nextButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val paymentMethodChangedAction = BehaviorSubject.create<PaymentMethod>()
        private val showError = BehaviorSubject.create<String>()
        private val finishPaymentProcessingAction = BehaviorSubject.create<Unit>()
        private val nextButtonLoadingIndicator = BehaviorSubject.create<Boolean>()
        private val paymentInformationChangedAction = BehaviorSubject.create<PaymentInformation>()

        private lateinit var vendor : Vendor
        private lateinit var paymentPayload: PaymentPayload
        private var amount : Float = 0F

        init {
            val payloadIntent = intent()
                .filter { it.hasExtra("SPSelectionObject") }
                .map {
                    //val obj = it.getSerializableExtra("SPSelectionObject") as SitePaySellerSelectionObject
                    //paymentPayload = obj.payload
                    //vendor = obj.vendor
                    return@map it.getSerializableExtra("SPSelectionObject") as SitePaySellerSelectionObject
                }
            val amountIntent = intent()
                .filter { it.hasExtra("amount") }
                .map {
                    amount = it.getFloatExtra("amount",0F)
                    return@map it.getFloatExtra("amount",0F)
                }

            payloadIntent.subscribe {
                paymentPayload = it.payload
                vendor = it.vendor
            }

            this.paymentMethodChanged
                .subscribe(this.paymentMethodChangedAction)

            this.paymentMethodChanged
                .map { return@map PaymentInformation(paymentPayload.rediPuntos,it.rewards, amount.toDouble()) }
                .subscribe(this.paymentInformationChangedAction)

            val form = Observable.combineLatest<PaymentMethod,PaymentInformation,Pair<PaymentMethod,PaymentInformation>>(this.paymentMethodChanged,this.paymentInformationChangedAction,
                BiFunction { t1, t2 ->
                Pair(t1, t2)
            })

            val requestPaymentEvent = form
                .takeWhen(this.nextButtonPressed)
                .flatMap {
                    val wayToPayInput = WayToPayInput(it.second.second.usedRediPoints,it.second.first.cardId,it.second.second.usedCardPoints,it.second.second.cardAmountToPay)
                    val requestPaymentInput = RequestPaymentInput(environment.currentUser().getCurrentUser()?.userId?:"",this.paymentPayload.order.pid,wayToPayInput)
                    return@flatMap this.requestPayment(requestPaymentInput)
                }
                .share()

            requestPaymentEvent
                .filter { it.isFail() }
                .map { return@map "Ocurrió un error al procesar el pago. Por favor intente de nuevo." }
                .subscribe(this.showError)

            requestPaymentEvent
                .filter { !it.isFail() }
                .map { Unit }
                .subscribe(this.finishPaymentProcessingAction)
        }


        override fun paymentMethodChanged(paymentMethod: PaymentMethod) {
            return this.paymentMethodChanged.onNext(paymentMethod)
        }

        override fun nextButtonPressed() {
            return this.nextButtonPressed.onNext(Unit)
        }

        override fun paymentMethodChangedAction(): Observable<PaymentMethod> = this.paymentMethodChangedAction

        override fun showError(): Observable<String?> = this.showError

        override fun finishPaymentProcessingAction(): Observable<Unit> = this.finishPaymentProcessingAction

        override fun nextButtonLoadingIndicator(): Observable<Boolean> = this.nextButtonLoadingIndicator

        override fun paymentInformationChangedAction(): Observable<PaymentInformation> = this.paymentInformationChangedAction

        private fun requestPayment(input : RequestPaymentInput) : Observable<Result<RequestPayment>>{
            return environment.userUseCase().requestPayment(input)
                .doOnComplete { this.nextButtonLoadingIndicator.onNext(false) }
                .doOnSubscribe { this.nextButtonLoadingIndicator.onNext(true) }
        }

        fun getPaymentMethods() : List<PaymentMethod>{
            return this.paymentPayload.paymentMethods
        }

        fun getgetVendor() : Vendor {
            return this.vendor
        }

        fun getAmount() : Float {
            return this.amount
        }



    }
}