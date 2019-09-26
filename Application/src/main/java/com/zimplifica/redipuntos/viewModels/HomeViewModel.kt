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
    }
    interface Outputs {
        fun showCompletePersonalInfoAlert() : Observable<VerificationStatus>

        /// Emits to go to complete personal info.
        fun goToCompletePersonalInfoScreen() : Observable<Unit>

        fun signOutAction() : Observable<Unit>
        fun addPaymentMethodAction() : Observable<Unit>
        fun showIdentityVerificationFailure() : Observable<Unit>
        fun showIdentityVerificationSuccess() : Observable<Pair<String,String>>
        fun showAlert() : Observable<Pair<String,String>>

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


        //Outputs
        private val signOutAction = PublishSubject.create<Unit>()
        private val showCompletePersonalInfoAlert = PublishSubject.create<VerificationStatus>()
        private val goToCompletePersonalInfoScreen : Observable<Unit>
        private val addPaymentMethodAction : Observable<Unit>

        private val showIdentityVerificationFailure = BehaviorSubject.create<Unit>()
        private val showIdentityVerificationSuccess = BehaviorSubject.create<Pair<String,String>>()
        private val showAlert = BehaviorSubject.create<Pair<String,String>>()
        //private val accountInformationResult = BehaviorSubject.create<UserInformationResult>()

        init {

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
                            val paymentId = event.id
                            environment.userUseCase().getTransactionById(paymentId)
                                .filter { !it.isFail() }
                                .map { it.successValue() }
                                .subscribe {
                                    if (it == null) return@subscribe
                                    environment.userUseCase().registerNewPayment(it)
                                }
                        }
                        event.type == "PaymentProcessed" -> {
                            val paymentId = event.id
                            Observables.zip(environment.userUseCase().getTransactionById(paymentId),environment.userUseCase().getUserInformation(false))
                                .map { it.first }
                                .filter { !it.isFail() }
                                .map { it.successValue() }
                                .subscribe { transition ->
                                    if (transition == null) return@subscribe
                                    environment.userUseCase().registerNewPayment(transition)
                                }
                        }
                        event.type == "Alert" -> {
                            Log.e("🔴","Alert.showAlert(title: event.title, message: event.message)")
                            this.showAlert.onNext(Pair(event.title,event.message))
                        }
                    }

                }


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

        override fun showIdentityVerificationFailure(): Observable<Unit> = this.showIdentityVerificationFailure

        override fun showIdentityVerificationSuccess(): Observable<Pair<String, String>> = this.showIdentityVerificationSuccess

        override fun showAlert(): Observable<Pair<String, String>> = this.showAlert

        private fun registDeviceToken(token : String) : Observable<Result<String>>{
            val userId = environment.currentUser().getCurrentUser()?.id
            return environment.userUseCase().registPushNotificationToken(token, userId ?: "")
        }

        private fun handleReviewMerchant(rateCommerceModel: RateCommerceModel) : Observable<Result<Boolean>>{
            return environment.userUseCase().reviewMerchant(rateCommerceModel)
        }
    }
}