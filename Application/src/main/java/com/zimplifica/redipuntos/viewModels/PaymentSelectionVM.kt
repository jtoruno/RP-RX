package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CheckAndPayModel
import io.reactivex.Observable
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

        /// Emits when the pin is requested.
        fun pinSecurityCodeRequest() : Observable<Unit>

        /// Emits when the BiometricAuth is requested.
        fun biometricAuthRequest() : Observable<Unit>

    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<PaymentSelectionVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        private val resources = RPApplication.applicationContext().resources

        private val securityMode = checkSecurityMode()
        private var _checkAndPayModel : CheckAndPayModel? = null
        private var checkAndPayModel : CheckAndPayModel
            set(value) {_checkAndPayModel = value; this.checkAndPayModelAction.onNext(value) }
            get() = _checkAndPayModel ?: throw UninitializedPropertyAccessException("this was queried")

        private val defaultPayment = PaymentMethod("","","","")

        //Inputs
        private val paymentMethodChanged = PublishSubject.create<PaymentMethod>()
        private val nextButtonPressed = PublishSubject.create<Unit>()
        private val applyRewardsRowPressed = PublishSubject.create<Boolean>()
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
        private val pinSecurityCodeRequest = BehaviorSubject.create<Unit>()
        private val biometricAuthRequest = BehaviorSubject.create<Unit>()

        //Helpers
        val pinSecurityCodeStatusAction = PublishSubject.create<Unit>()
        val biometricAuthStatusAction = PublishSubject.create<Unit>()


        init {
            val modelIntent = intent()
                .filter { it.hasExtra("CheckAndPayModel") }
                .map {
                    //this.checkAndPayModel = result
                    return@map it.getSerializableExtra("CheckAndPayModel") as CheckAndPayModel
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
                .map { return@map resources.getString(R.string.Error_add_payment_method_first) }
                .subscribe(this.showError)

            nextButtonPressed
                .filter { checkAndPayModel.selectedPaymentMethod!=null }
                .subscribe { return@subscribe this.handleSecurityVerification(securityMode) }



            val requestPayment = Observable.merge(this.biometricAuthStatusAction, this.pinSecurityCodeStatusAction)
                .flatMap {
                    val cardId = this.checkAndPayModel.selectedPaymentMethod?.cardId
                    var wayToPayInput = WayToPayInput(checkAndPayModel.rediPuntosToApply(),cardId,0.0,checkAndPayModel.chargeToApply())
                    val requestPaymentInput = RequestPaymentInput("",checkAndPayModel.orderId,wayToPayInput,checkAndPayModel.description)
                    return@flatMap requestPayment(requestPaymentInput)
                }
                .share()

            requestPayment
                .filter { it.isFail() }
                .map { return@map resources.getString(R.string.Error_processing_payment) }
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

        override fun pinSecurityCodeRequest(): Observable<Unit> = this.pinSecurityCodeRequest

        override fun biometricAuthRequest(): Observable<Unit> = this.biometricAuthRequest

        private fun handleSecurityVerification(securityMode: SecurityMode){
            when(securityMode){
                SecurityMode.biometricAuth -> {
                    //BiometricAuth
                    this.biometricAuthRequest.onNext(Unit)
                }SecurityMode.pinSecurityCode -> {
                    this.pinSecurityCodeRequest.onNext(Unit)
                }SecurityMode.none -> {
                    this.pinSecurityCodeRequest.onNext(Unit)
                }
            }
        }

        private fun checkSecurityMode() : SecurityMode {
            val biometricValue = environment.sharedPreferences().getBoolean(environment.currentUser().getCurrentUser()?.id ?: "", false)
            val pinCreated = environment.currentUser().getCurrentUser()?.securityCodeCreated ?: false
            return when {
                biometricValue -> SecurityMode.biometricAuth
                pinCreated -> SecurityMode.pinSecurityCode
                else -> SecurityMode.none
            }
        }


        private fun requestPayment(input : RequestPaymentInput) : Observable<Result<Transaction>>{
            return environment.userUseCase().requestPayment(input)
                .doOnComplete { this.nextButtonLoadingIndicator.onNext(false) }
                .doOnSubscribe { this.nextButtonLoadingIndicator.onNext(true) }
        }
    }
}