package com.zimplifica.redipuntos.viewModels

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.NonNull
import com.zimplifica.domain.entities.*
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

        // Call when change user password button is pressed.
        fun goToChangePasswordScreen()

        /// Call when change user passoword button is pressed.
        fun changePasswordButtonPressed()

        /// Call when pin button is pressed.
        fun pinButtonPressed()

        /// Call when user selects to update the pin in the alert.
        fun showUpdatePinScreen()

        /// Call when biometric authentication switch is changed.
        fun biometricAuthChanged(enabled: Boolean)

        /// Call when biometric authentication change is accepted.
        fun biometricAuthChangeAccepted(enabled: Boolean)

        /// Call when refer a friend button is pressed.
        fun referFriendButtonPressed()

        /// Call when promo code button is pressed.
        fun promoCodeButtonPressed()

        /// Call when enable premium button is pressed.
        fun enablePremiumButtonPressed()
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

        /// Emits when change user passoword is triggered.
        fun changePasswordButtonAction() : Observable<Unit>

        /// Emits when change user passoword is triggered and user confirms.
        fun goToChangePasswordScreenAction() : Observable<Unit>

        /// Emits when user already has a security code to alert the user.
        fun showUpdatePinAlert() : Observable<Unit>

        /// Emits when pin button is pressed.
        fun pinButtonAction() : Observable<PinRequestMode>

        /// Emits when an error ocurred.
        fun showBiometricAuthActivationAlert() : Observable<Boolean>

        /// Emits when the biometric authentication changes.
        fun biometricAuthEnabled() : Observable<Unit>

        /// Emits when the pin is requested.
        fun verifyPinSecurityCode() : Observable<Unit>

        /// Emits when refer a friend button is pressed.
        fun referFriendButtonAction() : Observable<Unit>

        /// Emits when promo code button is pressed.
        fun promoCodeButtonAction() : Observable<Unit>

        /// Emits when enable premium button is pressed.
        fun enablePremiumAction() : Observable<Unit>


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
        private val goToChangePasswordScreen = PublishSubject.create<Unit>()
        private val pinButtonPressed = PublishSubject.create<Unit>()
        private val showUpdatePinScreen = PublishSubject.create<Unit>()
        private val changePasswordButtonPressed = PublishSubject.create<Unit>()

        private val biometricAuthChanged = PublishSubject.create<Boolean>()
        private val biometricAuthChangeAccepted = PublishSubject.create<Boolean>()

        private val referFriendButtonPressed = PublishSubject.create<Unit>()
        private val promoCodeButtonPressed = PublishSubject.create<Unit>()
        private val enablePremiumButtonPressed = PublishSubject.create<Unit>()


        //Outputs
        private val accountInformationAction = BehaviorSubject.create<Boolean>()
        private val completeAccountInfoAction = BehaviorSubject.create<UserInformationResult>()
        private val updateUserInfo = BehaviorSubject.create<UserInformationResult>()
        private val showAlert = BehaviorSubject.create<Result<VerificationStatus>>()
        private val signOutAction = PublishSubject.create<Unit>()
        private val termsAndConditionsButton = BehaviorSubject.create<Unit>()
        private val privacyPolicyButton = BehaviorSubject.create<Unit>()
        private val aboutUsButton = BehaviorSubject.create<Unit>()
        private val changePasswordButtonAction = BehaviorSubject.create<Unit>()
        private val showUpdatePinAlert = BehaviorSubject.create<Unit>()
        private val pinButtonAction = BehaviorSubject.create<PinRequestMode>()
        private val goToChangePasswordScreenAction = BehaviorSubject.create<Unit>()

        private val showBiometricAuthActivationAlert = BehaviorSubject.create<Boolean>()
        private val biometricAuthEnabled = BehaviorSubject.create<Unit>()
        private val verifyPinSecurityCode = BehaviorSubject.create<Unit>()

        private val referFriendButtonAction = BehaviorSubject.create<Unit>()
        private val promoCodeButtonAction = BehaviorSubject.create<Unit>()
        private val enablePremiumAction = BehaviorSubject.create<Unit>()
        //Helper
        val pinSecurityCodeStatusAction = PublishSubject.create<Unit>()

        init {
            onCreate
                .flatMap { environment.userUseCase().getUserInformationSubscription() }
                .subscribe(this.updateUserInfo)

            accountInformationSelected
                .map { this.accountInfoEnabled() }
                .subscribe(this.accountInformationAction)

            completeAccountInfoButtonPressed
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

            pinButtonPressed
                .map { environment.currentUser().getCurrentUser()?.securityCodeCreated }
                .subscribe {userHasPin ->
                    if (userHasPin == true){
                        this.showUpdatePinAlert.onNext(Unit)
                    }else{
                        this.pinButtonAction.onNext(PinRequestMode.CREATE)
                    }
                }

            showUpdatePinScreen
                .map { PinRequestMode.UPDATE }
                .subscribe(this.pinButtonAction)

            //Biometric

            biometricAuthChangeAccepted
                .map { Unit }
                .subscribe(verifyPinSecurityCode)

            biometricAuthChanged
                .subscribe {enabled ->
                    this.biometricAuthEnabled.onNext(Unit)
                    if(enabled){
                        this.showBiometricAuthActivationAlert.onNext(enabled)
                    } else {
                        this.verifyPinSecurityCode.onNext(Unit)
                    }
                }

            pinSecurityCodeStatusAction
                .subscribe{
                    this.setupBiometricAuthentication()
                }

            onCreate
                .subscribe(this.biometricAuthEnabled)


            ///

            this.termsAndConditionsButtonPressed
                .subscribe(this.termsAndConditionsButton)

            this.privacyPolicyButtonPressed
                .subscribe(this.privacyPolicyButton)

            this.aboutUsButtonPressed
                .subscribe(this.aboutUsButton)

            this.goToChangePasswordScreen
                .subscribe(this.goToChangePasswordScreenAction)

            this.changePasswordButtonPressed
                .subscribe(this.changePasswordButtonAction)

            this.enablePremiumButtonPressed
                .subscribe(this.enablePremiumAction)

            this.promoCodeButtonPressed
                .subscribe(this.promoCodeButtonAction)

            this.referFriendButtonPressed
                .subscribe(this.referFriendButtonAction)
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

        override fun goToChangePasswordScreen() {
            return this.goToChangePasswordScreen.onNext(Unit)
        }

        override fun pinButtonPressed() {
            return this.pinButtonPressed.onNext(Unit)
        }

        override fun showUpdatePinScreen() {
            return this.showUpdatePinScreen.onNext(Unit)
        }

        override fun changePasswordButtonPressed() {
            return this.changePasswordButtonPressed.onNext(Unit)
        }

        override fun biometricAuthChanged(enabled: Boolean) {
            return this.biometricAuthChanged.onNext(enabled)
        }

        override fun biometricAuthChangeAccepted(enabled: Boolean) {
            return this.biometricAuthChangeAccepted.onNext(enabled)
        }

        override fun referFriendButtonPressed() {
            return this.referFriendButtonPressed.onNext(Unit)
        }

        override fun promoCodeButtonPressed() {
            return this.promoCodeButtonPressed.onNext(Unit)
        }

        override fun enablePremiumButtonPressed() {
            return this.enablePremiumButtonPressed.onNext(Unit)
        }

        override fun referFriendButtonAction(): Observable<Unit> = this.referFriendButtonAction

        override fun promoCodeButtonAction(): Observable<Unit> = this.promoCodeButtonAction

        override fun enablePremiumAction(): Observable<Unit> = this.enablePremiumAction

        override fun showBiometricAuthActivationAlert(): Observable<Boolean> = this.showBiometricAuthActivationAlert

        override fun biometricAuthEnabled(): Observable<Unit> = this.biometricAuthEnabled

        override fun verifyPinSecurityCode(): Observable<Unit> = this.verifyPinSecurityCode

        override fun goToChangePasswordScreenAction(): Observable<Unit> = this.goToChangePasswordScreenAction

        override fun showUpdatePinAlert(): Observable<Unit> = this.showUpdatePinAlert

        override fun pinButtonAction(): Observable<PinRequestMode> = this.pinButtonAction

        override fun changePasswordButtonAction(): Observable<Unit> = this.changePasswordButtonAction

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

        //BiometricAuth
        private fun setupBiometricAuthentication(){
            val currentUser = environment.currentUser().getCurrentUser() ?: return
            val enabled = environment.sharedPreferences().getBoolean(currentUser.id, false)
            val editor = environment.sharedPreferences().edit()
            with (editor){
                putBoolean(currentUser.id, !enabled)
                    .apply()
            }
            biometricAuthEnabled.onNext(Unit)
        }

        fun biometricAuthValue() : Boolean{
            return environment.sharedPreferences().getBoolean(environment.currentUser().getCurrentUser()?.id ?: "", false)
        }

    }
}