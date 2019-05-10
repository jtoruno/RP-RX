package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.ForgotPasswordError
import com.zimplifica.domain.entities.ForgotPasswordResult
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface ForgotPasswordViewModel {
    interface Inputs{
        fun usernameChanged(username: String)
        fun nextButtonPressed()
    }
    interface Outputs{
        fun nextButtonEnabled() : Observable<Boolean>
        fun loadingEnabled() : Observable<Boolean>
        fun showError() : Observable<ErrorWrapper>
        fun forgotPasswordStatus() : Observable<Unit>
    }

    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<ForgotPasswordViewModel>(environment),Inputs,Outputs{

        val inputs: Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val usernameChanged = PublishSubject.create<String>()
        private val nextButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private var nextButtonEnabled = BehaviorSubject.create<Boolean>()
        private var loadingEnabled = BehaviorSubject.create<Boolean>()
        private var showError = PublishSubject.create<ErrorWrapper>()
        private var forgotPasswordStatus = PublishSubject.create<Unit>()

        init {

            usernameChanged
                .map { ValidationService.validateUsername(it) }
                .subscribe(nextButtonEnabled)

            val forgotPasswordAcion = usernameChanged
                .takeWhen(nextButtonPressed)
                .flatMap { result ->
                    return@flatMap this.forgotPassword(result.second)
                }
                .share()
            forgotPasswordAcion
                .filter { it.isFail() }
                .map{it ->
                    when(it){
                        is Result.success -> return@map null
                        is Result.failure -> it.cause as? ForgotPasswordError
                    }
                }.filter { it!= null }
                .map { ErrorHandler.handleError(it , AuthenticationErrorType.FORGOT_PASSWORD_ERROR) }
                .subscribe(this.showError)

            forgotPasswordAcion
                .filter { !it.isFail() }
                .map { it->
                    when(it){
                        is Result.success -> return@map it.value as ForgotPasswordResult
                        is Result.failure -> return@map null
                    }
                }.filter { it!=null }
                .map { it-> Unit }
                .subscribe(this.forgotPasswordStatus)


        }


        override fun usernameChanged(username: String) = this.usernameChanged.onNext(username)

        override fun nextButtonPressed() = this.nextButtonPressed.onNext(Unit)

        override fun nextButtonEnabled(): Observable<Boolean> = this.nextButtonEnabled

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun showError(): Observable<ErrorWrapper> = this.showError

        override fun forgotPasswordStatus(): Observable<Unit> = this.forgotPasswordStatus

        private fun forgotPassword(username: String) : Observable<Result<ForgotPasswordResult>>{
            var usernameFormated = ""
            val credentialType = ValidationService.userCredentialType(username)
            usernameFormated = when(credentialType){
                UserCredentialType.PHONE_NUMBER -> "+506$username"
                UserCredentialType.EMAIL -> username
            }
            return environment.authenticationUseCase().forgotPassword(usernameFormated)
                .doOnComplete { this.loadingEnabled.onNext(false) }
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

    }
}