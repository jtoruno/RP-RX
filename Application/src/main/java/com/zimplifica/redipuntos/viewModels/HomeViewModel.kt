package com.zimplifica.redipuntos.viewModels

import android.annotation.SuppressLint
import androidx.annotation.NonNull
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.domain.entities.VerificationStatus
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.UserConfirmationStatus
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

            onCreate
                .map { return@map environment.currentUser().getCurrentUser()?.status?.status }
                .subscribe (this.showCompletePersonalInfoAlert)

            /*
            onCreate
                .map { return@map environment.currentUser().getCurrentUser() }
                .subscribe(this.accountInformationResult)

            environment.userUseCase().getUserInformationSubscription()
                .subscribe(this.accountInformationResult)*/

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
                        event.type == "VerificationFinished" -> {
                            val lastAccountStatus = environment.currentUser().getCurrentUser()?.status ?: VerificationStatus.Pending
                            environment.userUseCase().getUserInformation(false)
                                .filter { !it.isFail() }
                                .map { it.successValue() }
                                .subscribe { userInfo ->
                                    when(userInfo?.status?.status){
                                        VerificationStatus.Pending -> {
                                            Log.e("🔴","Alert.showIdentityVerificationFailureAlert()")
                                            this.showIdentityVerificationFailure.onNext(Unit)
                                        }
                                        VerificationStatus.Verifying -> {
                                            Log.e("🔴", "Verification pending after verification completed subscription")
                                        }
                                        VerificationStatus.VerifiedValid -> {
                                            if(lastAccountStatus != VerificationStatus.VerifiedValid){
                                                Log.e("🔴","Alert.showIdentityVerificationSuccessAlert(title: event.title, message: event.message)")
                                                this.showIdentityVerificationSuccess.onNext(Pair(event.title,event.message))
                                            }else{
                                                Log.e("🔸","Account activate notification but already triggered")
                                            }
                                        }
                                        VerificationStatus.VerifiedInvalid -> {
                                            Log.e("🔸","Account activate notification but already triggered")
                                        }
                                    }

                                }
                        }
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

        //override fun accountInformationResult(): Observable<UserInformationResult> = this.accountInformationResult


        private fun signOut() : Observable<Result<UserStateResult>>{
            return environment.authenticationUseCase().signOut()
        }

        override fun showIdentityVerificationFailure(): Observable<Unit> = this.showIdentityVerificationFailure

        override fun showIdentityVerificationSuccess(): Observable<Pair<String, String>> = this.showIdentityVerificationSuccess

        override fun showAlert(): Observable<Pair<String, String>> = this.showAlert

        private fun registDeviceToken(token : String) : Observable<Result<String>>{
            val userId = environment.currentUser().getCurrentUser()?.userId
            return environment.userUseCase().registPushNotificationToken(token, userId ?: "")
        }
    }
}