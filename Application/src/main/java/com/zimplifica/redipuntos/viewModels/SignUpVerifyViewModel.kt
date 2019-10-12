package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.*
import com.zimplifica.redipuntos.models.SignUpModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface SignUpVerifyViewModel {
    interface Inputs {
        fun verificationButtonPressed()
        fun verificationCodeTextChanged(verificationCode: String)
        fun resendVerificationCodePressed()
    }
    interface Outputs {
        fun verificationButtonEnabled() : Observable<Boolean>
        fun verifiedAction() : Observable<Unit>
        fun resendAction() : Observable<Unit>
        fun showError() : Observable<ErrorWrapper>
        fun loadingEnabled() : Observable<Boolean>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<SignUpVerifyViewModel>(environment), Inputs, Outputs{


        val inputs : Inputs = this
        val outputs : Outputs = this
        var username = ""

        //Inputs

        private val verificationButtonPressed = PublishSubject.create<Unit>()
        private val verificationCodeTextChanged = PublishSubject.create<String>()
        private val resendVerificationCodePressed = PublishSubject.create<Unit>()

        //Outputs
        private val verificationButtonEnabled = BehaviorSubject.create<Boolean>()
        private val verifiedAction = PublishSubject.create<Unit>()
        private val loadingEnabled = BehaviorSubject.create<Boolean>()
        private val resendAction = PublishSubject.create<Unit>()
        private val showError = PublishSubject.create<ErrorWrapper>()



        init {
            val model = intent()
                .filter{it.hasExtra("SignUpModel")}
                .map {
                    val result = it.getSerializableExtra("SignUpModel") as SignUpModel
                    username = result.phoneNumber
                    return@map result
                }

            val validCode = verificationCodeTextChanged
                .map{ValidationService.validateVerificationCode(it)}
            validCode.subscribe(this.verificationButtonEnabled)

            val modelAndCode = Observables.combineLatest(model,verificationCodeTextChanged)

            val verifyAndSignInEvent = modelAndCode
                .takeWhen(verificationButtonPressed)
                .flatMap { return@flatMap this.confirmSignUpAndSignIn(it.second.second,it.second.first) }
                .share()

            verifyAndSignInEvent
                .filter { !it.isFail() }
                .map { Unit }
                .subscribe(this.verifiedAction)

            verifyAndSignInEvent
                .filter { it.isFail() }
                .map {
                    when(it){
                        is Result.failure -> return@map it.cause
                        is Result.success -> return@map null
                    }
                }
                .map { ErrorHandler.handleError(it,AuthenticationErrorType.SIGN_UP_ERROR) }
                .subscribe(this.showError)

            val resendEvent = model
                .takeWhen(this.resendVerificationCodePressed)
                .flatMap { this.resendVerificationCode(it.second.phoneNumber) }
                .share()

            resendEvent
                .filter { !it.isFail() }
                .map { Unit }
                .subscribe(this.resendAction)

            resendEvent
                .filter { it.isFail() }
                .map {
                    when(it){
                        is Result.success -> return@map null
                        is Result.failure -> return@map it.cause as? SignUpError
                    } }
                .map { ErrorHandler.handleError(it , AuthenticationErrorType.SIGN_UP_ERROR) }
                .subscribe(this.showError)
        }



        override fun verificationButtonPressed() = this.verificationButtonPressed.onNext(Unit)

        override fun verificationCodeTextChanged(verificationCode: String) = this.verificationCodeTextChanged.onNext(verificationCode)

        override fun resendVerificationCodePressed() = this.resendVerificationCodePressed.onNext(Unit)

        override fun verificationButtonEnabled(): Observable<Boolean> = this.verificationButtonEnabled

        override fun verifiedAction(): Observable<Unit> = this.verifiedAction

        override fun resendAction(): Observable<Unit> = this.resendAction

        override fun showError(): Observable<ErrorWrapper> = this.showError

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled


        private fun confirmSignUpAndSignIn(verificationCode: String, model : SignUpModel) : Observable<Result<SignInResult>>{
            return Observable.create<Result<SignInResult>> create@{ observable ->

                val verifyEvent = environment.authenticationUseCase().signUp(model.userId,model.phoneNumberWithExtension(),model.password,verificationCode,model.nickname)
                    .share()


                verifyEvent
                    .filter { it.isFail() }
                    .map { it ->
                        when(it){
                            is Result.success -> return@map null
                            is Result.failure -> return@map it.cause as? SignUpError
                        }
                    }
                    .subscribe {
                        observable.onNext(Result.failure(it))
                        observable.onComplete()
                    }

                val signInEvent = verifyEvent
                    .filter { !it.isFail() }
                    .flatMap {
                        return@flatMap environment.authenticationUseCase().signIn(model.phoneNumberWithExtension(), model.password)
                    }

                signInEvent
                        .filter { it.isFail() }
                    .map { when(it) {
                        is Result.success -> return@map null
                        is Result.failure -> return@map it.cause as? SignInError
                    } }
                    .subscribe {
                        observable.onNext(Result.failure(it))
                        observable.onComplete()
                    }

                signInEvent
                    .filter { !it.isFail() }
                    .map { return@map it.successValue() }
                    .subscribe {
                        observable.onNext(Result.success(it))
                        observable.onComplete()
                    }

            }.doOnComplete { this.loadingEnabled.onNext(false) }.doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

        private fun resendVerificationCode(phoneNumber: String): Observable<Result<Boolean>>{
            var usernameFormated = ""
            val credentialType = ValidationService.userCredentialType(phoneNumber)
            usernameFormated = when(credentialType) {
                UserCredentialType.PHONE_NUMBER -> "+506$phoneNumber"
                UserCredentialType.EMAIL -> phoneNumber
            }
            return environment.authenticationUseCase().verifyPhoneNumber(usernameFormated)
                .doOnComplete { this.loadingEnabled.onNext(false) }
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

        private fun storeBiometricAuthTemporarily(enabled: Boolean){
            if (enabled){
                val editor = environment.sharedPreferences().edit()
                with (editor){
                    putBoolean("biometricStatusTemp", enabled)
                        .apply()
                }
            }
        }

    }
}