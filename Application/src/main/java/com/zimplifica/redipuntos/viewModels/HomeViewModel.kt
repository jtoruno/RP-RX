package com.zimplifica.redipuntos.viewModels

import android.annotation.SuppressLint
import androidx.annotation.NonNull
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.ui.data.maxCardsAllowed
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface HomeViewModel {
    interface Inputs {
        fun onCreate()
        fun completePersonalInfoButtonPressed()
        fun signOutButtonPressed()
        fun addPaymentButtonPressed()
        fun token(token: String)
        fun rateCommerceInput(model: RateCommerceModel)
    }
    interface Outputs {
        fun showCompletePersonalInfoAlert() : Observable<VerificationStatus>

        /// Emits to go to complete personal info.
        fun goToCompletePersonalInfoScreen() : Observable<Unit>

        fun signOutAction() : Observable<Unit>
        fun addPaymentMethodAction() : Observable<Unit>
        fun showAlert() : Observable<Pair<String,String>>
        fun showRateCommerceAlert() : Observable<RateCommerceModel>

        //fun accountInformationResult() : Observable<UserInformationResult>

    }
    @SuppressLint("CheckResult")
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<HomeViewModel>(environment), Inputs, Outputs{
        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()
        private val signOutButtonPressed = PublishSubject.create<Unit>()
        private val completePersonalInfoButtonPressed = PublishSubject.create<Unit>()
        private val addPaymentButtonPressed = PublishSubject.create<Unit>()
        private val tokenInput = PublishSubject.create<String>()
        private val rateCommerceInput = PublishSubject.create<RateCommerceModel>()


        //Outputs
        private val signOutAction = PublishSubject.create<Unit>()
        private val showCompletePersonalInfoAlert = PublishSubject.create<VerificationStatus>()
        private val goToCompletePersonalInfoScreen : Observable<Unit>
        private val addPaymentMethodAction : Observable<Unit>
        private val showAlert = BehaviorSubject.create<Pair<String,String>>()
        private val showRateCommerceAlert = BehaviorSubject.create<RateCommerceModel>()
        //private val accountInformationResult = BehaviorSubject.create<UserInformationResult>()

        init {

            onCreate
                .map { environment.currentUser().getCurrentUser() }
                .subscribe { userInfo ->
                    if(userInfo == null) return@subscribe
                    this.handleBiometricAuthSignUpEnabled(userInfo.id)
                    environment.userUseCase().initServerSubscription(userInfo.id)
                }

            val tokenEvent = tokenInput
                .flatMap { return@flatMap this.registDeviceToken(it) }
                .share()

            tokenEvent
                .filter { it.isFail() }
                .subscribe { Log.e("Error","Error with the token device") }

            tokenEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe {
                    Log.e("TokenCorrect",it)
                }


            val signOutEvent = signOutButtonPressed
                .flatMap { this.signOut() }
                .share()
            signOutEvent
                .map {  return@map  }
                .subscribe(this.signOutAction)

            this.goToCompletePersonalInfoScreen = this.completePersonalInfoButtonPressed
            this.addPaymentMethodAction = this.addPaymentButtonPressed

            environment.userUseCase().getActionableEventSubscription()
                .subscribe {event ->
                    if (event == null) return@subscribe
                    Log.e("🔵", "[HomeVM] [init] New actionable event $event")
                    when {
                        event.type == "PaymentRequested" -> {
                            val paymentId = event.origin
                            environment.userUseCase().getTransactionById(paymentId)
                                .filter { !it.isFail() }
                                .map { it.successValue() }
                                .subscribe {
                                    if (it == null) return@subscribe
                                    environment.userUseCase().registerNewPayment(it)
                                }
                        }
                        event.type == "PaymentProcessed" -> {
                            val paymentId = event.origin
                            Observables.zip(environment.userUseCase().getTransactionById(paymentId),environment.userUseCase().getUserInformation(false))
                                .map { it.first }
                                .filter { !it.isFail() }
                                .map { it.successValue() }
                                .subscribe { transition ->
                                    if (transition == null) return@subscribe
                                    environment.userUseCase().registerNewPayment(transition)
                                    val rateCommerceModel = RateCommerceModel(transition.id,transition.transactionDetail.vendorName,transition.date)
                                    this.showRateCommerceAlert.onNext(rateCommerceModel)
                                }
                        }
                        event.type == "Alert" -> {
                            Log.e("🔴","Alert.showAlert(title: event.title, message: event.message)")
                            this.showAlert.onNext(Pair(event.title,event.message))
                        }
                    }
                }

            this.rateCommerceInput
                .flatMap {
                    Log.e("RateCommerce",it.commerceName + it.rate.name)
                    return@flatMap this.handleReviewMerchant(it)
                }
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe()


        }

        override fun onCreate() {
            this.onCreate.onNext(Unit)
        }

        override fun addPaymentButtonPressed() {
            val paymentMethods = environment.currentUser().getCurrentUser()?.paymentMethods?.size ?: return
            if (paymentMethods < maxCardsAllowed){
                return addPaymentButtonPressed.onNext(Unit)
            }
        }

        override fun token(token: String) {
            this.tokenInput.onNext(token)
        }

        override fun addPaymentMethodAction(): Observable<Unit> = this.addPaymentMethodAction

        override fun completePersonalInfoButtonPressed() {
            return this.completePersonalInfoButtonPressed.onNext(Unit)
        }

        override fun showCompletePersonalInfoAlert(): Observable<VerificationStatus> = this.showCompletePersonalInfoAlert

        override fun goToCompletePersonalInfoScreen(): Observable<Unit> {
            return this.goToCompletePersonalInfoScreen
        }

        override fun signOutButtonPressed() {
            return this.signOutButtonPressed.onNext(Unit)
        }

        override fun signOutAction(): Observable<Unit> = this.signOutAction


        private fun signOut() : Observable<Result<UserStateResult>>{
            return environment.authenticationUseCase().signOut()
        }

        override fun showAlert(): Observable<Pair<String, String>> = this.showAlert

        override fun rateCommerceInput(model: RateCommerceModel) {
            Log.e("rateCommerceInput",model.rate.name)
            return this.rateCommerceInput.onNext(model)
        }

        override fun showRateCommerceAlert(): Observable<RateCommerceModel> = this.showRateCommerceAlert

        private fun registDeviceToken(token : String) : Observable<Result<String>>{
            val userId = environment.currentUser().getCurrentUser()?.id
            return environment.userUseCase().registPushNotificationToken(token, userId ?: "")
        }

        private fun handleReviewMerchant(rateCommerceModel: RateCommerceModel) : Observable<Result<Boolean>>{
            Log.e("handleReviewMerchant",rateCommerceModel.rate.name)
            return environment.userUseCase().reviewMerchant(rateCommerceModel)
        }

        private fun handleBiometricAuthSignUpEnabled(userId: String){
            val biometricStatusTemp = environment.sharedPreferences().contains("biometricStatusTemp")
            if (biometricStatusTemp){
                Log.e("HomeVM","handleBiometricAuthSignUpEnabled")
                val biometricStatus = environment.sharedPreferences().getBoolean("biometricStatusTemp",false)
                val editor = environment.sharedPreferences().edit()
                with (editor){
                    putBoolean(userId, biometricStatus)
                        .apply()
                }
                with(editor){
                    remove("biometricStatusTemp")
                        .apply()
                }
            }
        }
    }
}