package com.zimplifica.redipuntos.viewModels

import android.annotation.SuppressLint
import androidx.annotation.NonNull
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.UserConfirmationStatus
import com.zimplifica.redipuntos.ui.data.maxCardsAllowed
import io.reactivex.Observable
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
        fun showCompletePersonalInfoAlert() : Observable<Unit>

        /// Emits to go to complete personal info.
        fun goToCompletePersonalInfoScreen() : Observable<Unit>

        fun signOutAction() : Observable<Unit>
        fun addPaymentMethodAction() : Observable<Unit>

        fun accountInformationResult() : Observable<UserInformationResult>

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
        private val showCompletePersonalInfoAlert = PublishSubject.create<Unit>()
        private val goToCompletePersonalInfoScreen : Observable<Unit>
        private val addPaymentMethodAction : Observable<Unit>

        private val accountInformationResult = BehaviorSubject.create<UserInformationResult>()

        init {

            val event = tokenInput
                .flatMap { return@flatMap this.registDeviceToken(it) }
                .share()

            event
                .filter { it.isFail() }
                .subscribe { Log.e("Error","Error with the token device") }

            event
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe {
                    Log.e("TokenCorrect",it)
                }

            onCreate
                .map { return@map environment.currentUser().userConfirmationStatus() }
                .subscribe {
                    Log.e("Status",it?.confirmationStatus.toString())
                    when(it?.confirmationStatus){
                        UserConfirmationStatus.ConfirmationStatus.missingInfo ->{
                            this.showCompletePersonalInfoAlert.onNext(Unit)
                        }
                        else -> {
                        }
                    }
                }

            onCreate
                .map { return@map environment.currentUser().getCurrentUser() }
                .subscribe(this.accountInformationResult)

            environment.userUseCase().getUserInformationSubscription()
                .subscribe(this.accountInformationResult)

            val signOutEvent = signOutButtonPressed
                .flatMap { this.signOut() }
                .share()
            signOutEvent
                .map {  return@map  }
                .subscribe(this.signOutAction)

            this.goToCompletePersonalInfoScreen = this.completePersonalInfoButtonPressed
            this.addPaymentMethodAction = this.addPaymentButtonPressed



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

        override fun showCompletePersonalInfoAlert(): Observable<Unit> = this.showCompletePersonalInfoAlert

        override fun goToCompletePersonalInfoScreen(): Observable<Unit> {
            return this.goToCompletePersonalInfoScreen
        }

        override fun signOutButtonPressed() {
            return this.signOutButtonPressed.onNext(Unit)
        }

        override fun signOutAction(): Observable<Unit> = this.signOutAction

        override fun accountInformationResult(): Observable<UserInformationResult> = this.accountInformationResult


        private fun signOut() : Observable<Result<UserStateResult>>{
            return environment.authenticationUseCase().signOut()
        }

        private fun formatFloatToString(mFloat : Float): String{
            return "₡"+String.format("%,.1f", mFloat)
        }

        private fun registDevice(){


        }
        private fun registDeviceToken(token : String) : Observable<Result<String>>{
            val userId = environment.currentUser().getCurrentUser()?.userId
            return environment.userUseCase().registPushNotificationToken(token, userId ?: "")
        }
    }
}