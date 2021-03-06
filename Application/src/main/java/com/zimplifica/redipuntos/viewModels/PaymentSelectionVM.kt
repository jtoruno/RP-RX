package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CheckAndPayModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface PaymentSelectionVM {
    interface Inputs {
        fun paymentMethodChanged(paymentMethod: PaymentMethod)
        fun nextButtonPressed()
        fun applyRewardsRowPressed(state : Boolean)
        fun descriptionTextFieldChanged(description: String)
        fun addPaymentMethodButtonPressed()
        fun reloadPaymentMethods(paymentMethod: PaymentMethod)
    }
    interface Outputs {
        fun paymentMethodChangedAction() : Observable<PaymentMethod>
        fun showError() : Observable<String>

        /// Emits when the payment was processed.
        fun finishPaymentProcessingAction() : Observable<Transaction>

        /// Emits when the next button loading indicator changes.
        fun nextButtonLoadingIndicator() : Observable<Boolean>

        /// Emits when the payment method is changed.
        fun checkAndPayModelAction() : Observable<CheckAndPayModel>

        /// Emits when user press add payment method.
        fun addPaymentMethodAction() : Observable<Unit>

        /// Emits when the user adds a payment method and needs to reload the payment methods.
        fun reloadPaymentMethodsAction() : Observable<CheckAndPayModel?>

        fun applyRewards() : Observable<Boolean>

    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<PaymentSelectionVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        private var _checkAndPayModel : CheckAndPayModel? = null
        private var checkAndPayModel : CheckAndPayModel
            set(value) {_checkAndPayModel = value; this.checkAndPayModelAction.onNext(value) }
            get() = _checkAndPayModel ?: throw UninitializedPropertyAccessException("this was queried")

        private val defaultPayment = PaymentMethod("","","","",0.0,false)

        //Inputs
        private val paymentMethodChanged = PublishSubject.create<PaymentMethod>()
        private val nextButtonPressed = PublishSubject.create<Unit>()
        private val applyRewardsRowPressed = BehaviorSubject.createDefault(false)
        private val descriptionTextFieldChanged = BehaviorSubject.createDefault("")
        private val addPaymentMethodButtonPressed = PublishSubject.create<Unit>()
        private val reloadPaymentMethods = PublishSubject.create<PaymentMethod>()


        //Outputs
        private val paymentMethodChangedAction = BehaviorSubject.create<PaymentMethod>()
        private val showError = BehaviorSubject.create<String>()
        private val finishPaymentProcessingAction = BehaviorSubject.create<Transaction>()
        private val nextButtonLoadingIndicator = BehaviorSubject.create<Boolean>()
        private val checkAndPayModelAction = BehaviorSubject.create<CheckAndPayModel>()
        private val applyRewards = BehaviorSubject.create<Boolean>()
        private val addPaymentMethodAction = BehaviorSubject.create<Unit>()
        private val reloadPaymentMethodsAction = BehaviorSubject.create<CheckAndPayModel?>()


        init {
            val modelIntent = intent()
                .filter { it.hasExtra("CheckAndPayModel") }
                .map {
                    val result = it.getSerializableExtra("CheckAndPayModel") as CheckAndPayModel
                    //this.checkAndPayModel = result
                    return@map result
                }

            modelIntent
                .subscribe {
                    this.checkAndPayModel = it
                }

            environment.userUseCase().getPaymentMethodsSubscription()
                .subscribe(this.reloadPaymentMethods)

            this.paymentMethodChanged
                .subscribe(this.paymentMethodChangedAction)

            this.addPaymentMethodButtonPressed
                .subscribe(this.addPaymentMethodAction)

            applyRewardsRowPressed
                .subscribe(this.applyRewards)

            nextButtonPressed
                .filter { checkAndPayModel.selectedPaymentMethod == null }
                .map { return@map "Por favor seleccione un método de pago." }
                .subscribe(this.showError)

            val requestPayment = nextButtonPressed
                .filter { checkAndPayModel.selectedPaymentMethod!=null }
                .flatMap {
                    val cardId = this.checkAndPayModel.selectedPaymentMethod?.cardId
                    var wayToPayInput = WayToPayInput(checkAndPayModel.rediPuntosToApply(),cardId,0.0,checkAndPayModel.chargeToApply())
                    val requestPaymentInput = RequestPaymentInput("",checkAndPayModel.orderId,wayToPayInput,checkAndPayModel.description)
                    return@flatMap requestPayment(requestPaymentInput)
                }
                .share()

            requestPayment
                .filter { it.isFail() }
                .map { return@map "Ocurrió un error al procesar el pago. Por favor intente de nuevo." }
                .subscribe(this.showError)

            requestPayment
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.finishPaymentProcessingAction)

            reloadPaymentMethods
                .map {
                    val temporaryPaymentMethods = checkAndPayModel.paymentMethods.toMutableList()
                    temporaryPaymentMethods.add(it)
                    checkAndPayModel.paymentMethods = temporaryPaymentMethods
                    val firstPaymentMethod = checkAndPayModel.paymentMethods.first()
                    if (firstPaymentMethod != null){
                        checkAndPayModel.selectedPaymentMethod = firstPaymentMethod
                    }
                    return@map checkAndPayModelAction.value
                }
                .subscribe(this.reloadPaymentMethodsAction)

        }

        override fun applyRewardsRowPressed(state: Boolean) {
            checkAndPayModel.applyRediPuntos = state
            return this.applyRewardsRowPressed.onNext(state)
        }

        override fun applyRewards(): Observable<Boolean> = this.applyRewards


        override fun paymentMethodChanged(paymentMethod: PaymentMethod) {
            checkAndPayModel.selectedPaymentMethod = paymentMethod
            return this.paymentMethodChanged.onNext(paymentMethod)
        }

        override fun nextButtonPressed() {
            return this.nextButtonPressed.onNext(Unit)
        }

        override fun descriptionTextFieldChanged(description: String) {
            checkAndPayModel.description = description
            return this.descriptionTextFieldChanged.onNext(description)
        }

        override fun paymentMethodChangedAction(): Observable<PaymentMethod> = this.paymentMethodChangedAction

        override fun showError(): Observable<String> = this.showError

        override fun finishPaymentProcessingAction(): Observable<Transaction> = this.finishPaymentProcessingAction

        override fun nextButtonLoadingIndicator(): Observable<Boolean> = this.nextButtonLoadingIndicator

        override fun addPaymentMethodButtonPressed() {
            return this.addPaymentMethodButtonPressed.onNext(Unit)
        }

        override fun reloadPaymentMethods(paymentMethod: PaymentMethod) {
            return this.reloadPaymentMethods.onNext(paymentMethod)
        }

        override fun checkAndPayModelAction(): Observable<CheckAndPayModel> = this.checkAndPayModelAction

        override fun addPaymentMethodAction(): Observable<Unit> = this.addPaymentMethodAction

        override fun reloadPaymentMethodsAction(): Observable<CheckAndPayModel?> = this.reloadPaymentMethodsAction


        private fun requestPayment(input : RequestPaymentInput) : Observable<Result<Transaction>>{
            return environment.userUseCase().requestPayment(input)
                .doOnComplete { this.nextButtonLoadingIndicator.onNext(false) }
                .doOnSubscribe { this.nextButtonLoadingIndicator.onNext(true) }
        }
    }
}