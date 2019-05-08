package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.AuthenticationErrorType
import com.zimplifica.redipuntos.libs.utils.ErrorHandler
import com.zimplifica.redipuntos.libs.utils.ErrorWrapper
import com.zimplifica.redipuntos.libs.utils.ValidationService
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

        private val username = environment.sharedPreferences().getString("phoneNumber","")
        private val userId = environment.sharedPreferences().getString("userId", "")
        private val password = environment.sharedPreferences().getString("userId", "")

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
            verificationCodeTextChanged
                .map{ValidationService.validateVerificationCode(it)}
                .subscribe(this.verificationButtonEnabled)

            val verifyEvent = verificationCodeTextChanged
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
                    .map { it ->
                        when(it){
                            is Result.success -> return@map null
                            is Result.failure -> return@map it.cause as? SignUpError
                        }  }
                    .filter { it!= null }
                    .map { ErrorHandler.handleError(it , AuthenticationErrorType.SIGN_UP_ERROR) },
                resendEvent
                    .map { it ->
                        when(it){
                            is Result.success -> return@map null
                            is Result.failure -> return@map it.cause as? SignUpError
                        } }
                    .filter { it!= null }
                    .map { ErrorHandler.handleError(it , AuthenticationErrorType.SIGN_UP_ERROR) })
                .subscribe(this.showError)

            resendEvent
                .filter { !it.isFail() }
                .map { it ->
                    when(it){
                        is Result.success -> return@map it.value as SignUpResendConfirmationResult
                        is Result.failure -> return@map null
                    }
                }
                .filter { it!=null }
                .map { it -> Unit }
                .subscribe(this.resendAction)
        }



        override fun verificationButtonPressed() = this.verificationButtonPressed.onNext(Unit)

        override fun verificationCodeTextChanged(verificationCode: String) = this.verificationCodeTextChanged.onNext(verificationCode)

        override fun resendVerificationCodePressed() = this.resendVerificationCodePressed.onNext(Unit)

        override fun verificationButtonEnabled(): Observable<Boolean> = this.verificationButtonEnabled

        override fun verifiedAction(): Observable<Unit> = this.verifiedAction

        override fun resendAction(): Observable<Unit> = this.resendAction

        override fun showError(): Observable<ErrorWrapper> = this.showError

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        private fun confirmSignUp(userId: String, verificationCode: String) : Observable<Result<SignUpConfirmationResult>>{
            return environment.authenticationUseCase().confirmSignUp(userId,verificationCode)
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

        private fun signIn(username: String, password: String) : Observable<Result<SignInResult>>{
            return environment.authenticationUseCase().signIn(username, password)
                .doOnComplete { this.loadingEnabled.onNext(false) }
        }

        private fun resendVerificationCode(userId: String): Observable<Result<SignUpResendConfirmationResult>>{
            return environment.authenticationUseCase().resendVerificationCode(userId)
        }

        fun getUserName() : String{
            return this.username
        }

        fun getUserId() : String {
            return this.userId
        }

    }
}