package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.*
import io.reactivex.Observable
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

        var username : String = environment.sharedPreferences().getString("phoneNumber","")?:"default"
        var userId : String = environment.sharedPreferences().getString("userId", "")?:"default"
        var password : String = environment.sharedPreferences().getString("password", "")?:"default"

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

            val validCode = verificationCodeTextChanged
                .map{ValidationService.validateVerificationCode(it)}
            validCode.subscribe(this.verificationButtonEnabled)

            val verifyAndSignInEvent = verificationCodeTextChanged
                .takeWhen(verificationButtonPressed)
                .flatMap { return@flatMap this.confirmSignUpAndSignIn(it.second) }
                .share()

            verifyAndSignInEvent
                .filter { !it.isFail() }
                .map { it -> Unit }
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

            val resendEvent = resendVerificationCodePressed
                .flatMap { this.resendVerificationCode(username) }
                .share()

            resendEvent
                .filter { !it.isFail() }
                .map { it -> Unit }
                .subscribe(this.resendAction)

            resendEvent
                .filter { it.isFail() }
                .map { it ->
                    when(it){
                        is Result.success -> return@map null
                        is Result.failure -> return@map it.cause as? SignUpError
                    } }
                .map { ErrorHandler.handleError(it , AuthenticationErrorType.SIGN_UP_ERROR) }
                .subscribe(this.showError)

            /*val verifyEvent = verificationCodeTextChanged
                .takeWhen(this.verificationButtonPressed)
                .flatMap { it -> this.confirmSignUp(this.userId,it.second) }
                .share()
            verifyEvent
                .filter { !it.isFail() }
                .map { it ->
                    when(it){
                        is Result.success -> return@map it.value as SignUpConfirmationResult
                        is Result.failure -> return@map null
                    }
                }
                .filter { it!=null }
                .flatMap { this.signIn(this.username, this.password) }
                .map { it -> Unit }
                .subscribe(this.verifiedAction)

            val resendEvent = resendVerificationCodePressed
                .flatMap { it -> this.resendVerificationCode(this.userId) }
                .share()

            Observable.merge(
                verifyEvent
                    .filter { it.isFail() }
                    .map { it ->
                        when(it){
                            is Result.success -> return@map null
                            is Result.failure -> return@map it.cause as? SignUpError
                        }  }
                    .filter { it!= null }
                    .map { ErrorHandler.handleError(it , AuthenticationErrorType.SIGN_UP_ERROR) },
                resendEvent
                    .filter { it.isFail() }
                    .map { it ->
                        when(it){
                            is Result.success -> return@map null
                            is Result.failure -> return@map it.cause as? SignUpError
                        } }
                    .filter { it!= null }
                    .map { ErrorHandler.handleError(it , AuthenticationErrorType.SIGN_UP_ERROR) })
                .subscribe(this.showError)*/
        }



        override fun verificationButtonPressed() = this.verificationButtonPressed.onNext(Unit)

        override fun verificationCodeTextChanged(verificationCode: String) = this.verificationCodeTextChanged.onNext(verificationCode)

        override fun resendVerificationCodePressed() = this.resendVerificationCodePressed.onNext(Unit)

        override fun verificationButtonEnabled(): Observable<Boolean> = this.verificationButtonEnabled

        override fun verifiedAction(): Observable<Unit> = this.verifiedAction

        override fun resendAction(): Observable<Unit> = this.resendAction

        override fun showError(): Observable<ErrorWrapper> = this.showError

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        private fun confirmSignUp(verificationCode: String) : Observable<Result<SignUpResult>>{
            var usernameFormated = ""
            val credentialType = ValidationService.userCredentialType(username)
            usernameFormated = when(credentialType) {
                UserCredentialType.PHONE_NUMBER -> "+506$username"
                UserCredentialType.EMAIL -> username
            }
            return environment.authenticationUseCase().signUp(userId,usernameFormated,password,verificationCode)
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }


        private fun confirmSignUpAndSignIn(verificationCode: String) : Observable<Result<SignInResult>>{
            return Observable.create<Result<SignInResult>> create@{ observable ->

                var usernameFormated = ""
                val credentialType = ValidationService.userCredentialType(username)
                usernameFormated = when(credentialType) {
                    UserCredentialType.PHONE_NUMBER -> "+506$username"
                    UserCredentialType.EMAIL -> username
                }

                val verifyEvent = environment.authenticationUseCase().signUp(userId,usernameFormated,password,verificationCode)
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
                        return@flatMap environment.authenticationUseCase().signIn(usernameFormated, it.successValue()?.password!!)
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

        fun getUserName() : String{
            return this.username
        }

        fun getUserUUid() : String {
            return this.userId
        }

    }
}