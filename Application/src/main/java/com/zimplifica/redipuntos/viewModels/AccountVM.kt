package com.zimplifica.redipuntos.viewModels

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.domain.entities.VerificationStatus
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.FragmentViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface AccountVM {
    interface Inputs {
        fun onCreate()
        fun accountInformationSelected()
        fun completeAccountInfoButtonPressed()
        fun signOutButtonPressed()
        /// Call when terms and conditions button is pressed.
        fun termsAndConditionsButtonPressed()

        /// Call when privacy policy button is pressed.
        fun privacyPolicyButtonPressed()

        /// Call when about us button is pressed.
        fun aboutUsButtonPressed()
    }
    interface Outputs {
        fun accountInformationAction() : Observable<Boolean>
        fun completeAccountInfoAction() : Observable<UserInformationResult>
        fun updateUserInfo() : Observable<UserInformationResult>
        fun showAlert() : Observable<Result<VerificationStatus>>
        fun signOutAction() : Observable<Unit>
        /// Emits when terms and conditions button is pressed.
        fun termsAndConditionsButton() : Observable<Unit>

        /// Emits when privacy policy button is pressed.
        fun privacyPolicyButton() : Observable<Unit>

        /// Emits when about us button is pressed.
        fun aboutUsButton() : Observable<Unit>

    }
    @SuppressLint("CheckResult")
    class ViewModel(@NonNull val environment: Environment) : FragmentViewModel<AccountVM>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()
        private val accountInformationSelected = PublishSubject.create<Unit>()
        private val completeAccountInfoButtonPressed = PublishSubject.create<Unit>()
        private val signOutButtonPressed = PublishSubject.create<Unit>()
        private val termsAndConditionsButtonPressed = PublishSubject.create<Unit>()
        private val privacyPolicyButtonPressed = PublishSubject.create<Unit>()
        private val aboutUsButtonPressed = PublishSubject.create<Unit>()


        //Outputs
        private val accountInformationAction = BehaviorSubject.create<Boolean>()
        private val completeAccountInfoAction = BehaviorSubject.create<UserInformationResult>()
        private val updateUserInfo = BehaviorSubject.create<UserInformationResult>()
        private val showAlert = BehaviorSubject.create<Result<VerificationStatus>>()
        private val signOutAction = PublishSubject.create<Unit>()
        private val termsAndConditionsButton = BehaviorSubject.create<Unit>()
        private val privacyPolicyButton = BehaviorSubject.create<Unit>()
        private val aboutUsButton = BehaviorSubject.create<Unit>()

        init {
            onCreate
                .flatMap { environment.userUseCase().getUserInformationSubscription() }
                .subscribe(this.updateUserInfo)

            accountInformationSelected
                .map { this.accountInfoEnabled() }
                .subscribe(this.accountInformationAction)

            completeAccountInfoButtonPressed
                .debounce(100,TimeUnit.MILLISECONDS,AndroidSchedulers.mainThread())
                .flatMap { fetchUserInfo() }
                .subscribe {
                    when(it){
                        is Result.success -> {
                            val userInfo = it.value
                            this.completeAccountInfoAction.onNext(userInfo!!)
                        }
                        is Result.failure -> {
                            val error = it.cause
                            this.showAlert.onNext(Result.failure(error))
                        }
                    }
                }

            val signOutEvent = signOutButtonPressed
                .flatMap { this.signOut() }
                .share()
            signOutEvent
                .map {  return@map  }
                .subscribe(this.signOutAction)

            this.termsAndConditionsButtonPressed
                .subscribe(this.termsAndConditionsButton)

            this.privacyPolicyButtonPressed
                .subscribe(this.privacyPolicyButton)

            this.aboutUsButtonPressed
                .subscribe(this.aboutUsButton)
        }


        override fun onCreate() {
            this.onCreate.onNext(Unit)
        }

        override fun accountInformationSelected() {
            this.accountInformationSelected.onNext(Unit)
        }

        override fun completeAccountInfoButtonPressed() {
            this.completeAccountInfoButtonPressed.onNext(Unit)
        }

        override fun signOutButtonPressed() {
            return this.signOutButtonPressed.onNext(Unit)
        }

        override fun accountInformationAction(): Observable<Boolean> = this.accountInformationAction

        override fun completeAccountInfoAction(): Observable<UserInformationResult> = this.completeAccountInfoAction

        override fun updateUserInfo(): Observable<UserInformationResult> = this.updateUserInfo

        override fun showAlert(): Observable<Result<VerificationStatus>> = this.showAlert

        override fun signOutAction(): Observable<Unit> = this.signOutAction

        override fun termsAndConditionsButtonPressed() {
            this.termsAndConditionsButtonPressed.onNext(Unit)
        }

        override fun privacyPolicyButtonPressed() {
            this.privacyPolicyButtonPressed.onNext(Unit)
        }

        override fun aboutUsButtonPressed() {
            this.aboutUsButtonPressed.onNext(Unit)
        }

        override fun termsAndConditionsButton(): Observable<Unit> = this.termsAndConditionsButton

        override fun privacyPolicyButton(): Observable<Unit> = this.privacyPolicyButton

        override fun aboutUsButton(): Observable<Unit> = this.aboutUsButtonPressed

        private fun signOut() : Observable<Result<UserStateResult>>{
            return environment.authenticationUseCase().signOut()
        }

        private fun accountInfoEnabled() : Boolean{
            return environment.currentUser().getCurrentUser()?.nickname!=null
        }

        private fun fetchUserInfo() : Observable<Result<UserInformationResult>>{
            return environment.userUseCase().getUserInformation(false)
        }

    }
}