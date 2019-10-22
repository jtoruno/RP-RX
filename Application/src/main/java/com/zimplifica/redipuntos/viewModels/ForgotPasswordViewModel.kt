package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.ForgotPasswordError
import com.zimplifica.domain.entities.ForgotPasswordResult
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.*
import com.zimplifica.redipuntos.ui.data.contactEmail
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
        fun showError() : Observable<String>
        fun forgotPasswordStatus() : Observable<String>
    }

    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<ForgotPasswordViewModel>(environment),Inputs,Outputs{

        val inputs: Inputs = this
        val outputs : Outputs = this
        private val resources = RPApplication.applicationContext().resources

        //Inputs
        private val usernameChanged = BehaviorSubject.create<String>()
        private val nextButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private var nextButtonEnabled = BehaviorSubject.create<Boolean>()
        private var loadingEnabled = BehaviorSubject.create<Boolean>()
        private var showError = PublishSubject.create<String>()
        private var forgotPasswordStatus = PublishSubject.create<String>()

        init {

            usernameChanged
                .map { ValidationService.validateUsername(it) }
                .subscribe(nextButtonEnabled)

            val forgotPasswordAction = usernameChanged
                .takeWhen(nextButtonPressed)
                .flatMap { result ->
                    return@flatMap this.initForgotPassword(result.second)
                }
                .share()

            forgotPasswordAction
                .filter { it.isFail() }
                    /*
                .map{
                    when(it){
                        is Result.success -> return@map null
                        is Result.failure -> return@map it.cause as? ForgotPasswordError
                    }
                }
                .map { ErrorHandler.handleError(it , AuthenticationErrorType.FORGOT_PASSWORD_ERROR) }

                     */
                .map { resources.getString(R.string.Error_unkown_error, contactEmail) }
                .subscribe(this.showError)

            forgotPasswordAction
                .filter { !it.isFail() }
                .map {
                    when(it){
                        is Result.success -> return@map it.value
                        is Result.failure -> return@map null
                    }
                }
                .map {
                    val username = this.usernameChanged.value ?: ""
                    return@map ValidationService.normalizePhoneNumber(username)?: ""
                }
                .subscribe(this.forgotPasswordStatus)


        }


        override fun usernameChanged(username: String) = this.usernameChanged.onNext(username)

        override fun nextButtonPressed() = this.nextButtonPressed.onNext(Unit)

        override fun nextButtonEnabled(): Observable<Boolean> = this.nextButtonEnabled

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun showError(): Observable<String> = this.showError

        override fun forgotPasswordStatus(): Observable<String> = this.forgotPasswordStatus

        private fun initForgotPassword(username: String) : Observable<Result<Boolean>>{
            var usernameFormated = ""
            val credentialType = ValidationService.userCredentialType(username)
            usernameFormated = when(credentialType){
                UserCredentialType.PHONE_NUMBER -> "+506$username"
                UserCredentialType.EMAIL -> username
            }
            return environment.userUseCase().initForgotPassword(usernameFormated)
                .doOnComplete { this.loadingEnabled.onNext(false) }
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

    }
}