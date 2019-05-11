package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.ForgotPasswordError
import com.zimplifica.domain.entities.ForgotPasswordResult
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.AuthenticationErrorType
import com.zimplifica.redipuntos.libs.utils.ErrorHandler
import com.zimplifica.redipuntos.libs.utils.ErrorWrapper
import com.zimplifica.redipuntos.libs.utils.ValidationService
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.regex.Pattern

interface ConfirmForgotPsswordVM {
    interface Inputs {
        fun passwordChanged(password: String)
        fun confirmationCodeTextChanged(verificationCode: String)
        fun nextButtonPressed()
    }

    interface Outputs {
        fun nextButtonEnabled() : Observable<Boolean>
        fun passwordChangedAction() : Observable<Unit>
        fun showError() : Observable<ErrorWrapper>
        fun loadingEnabled() : Observable<Boolean>

        fun validPasswordLenght() : Observable<Boolean>
        fun validPasswordCapitalLowerLetters() : Observable<Boolean>
        fun validPasswordNumbers() : Observable<Boolean>
        fun validPasswordSpecialCharacters() : Observable<Boolean>
    }

    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<ConfirmForgotPsswordVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this
        var username : String = ""

        //Inputs
        private val passwordChanged = PublishSubject.create<String>()
        private val confirmationCodeTextChanged = PublishSubject.create<String>()
        private val nextButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val nextButtonEnabled = BehaviorSubject.create<Boolean>()
        private val validPasswordCapitalLowerLetters = BehaviorSubject.create<Boolean>()
        private val validPasswordLenght = BehaviorSubject.create<Boolean>()
        private val validPasswordNumbers = BehaviorSubject.create<Boolean>()
        private val validPasswordSpecialCharacters = BehaviorSubject.create<Boolean>()
        private val passwordChangedAction = PublishSubject.create<Unit>()
        private val showError = PublishSubject.create<ErrorWrapper>()
        private val loadingEnabled = BehaviorSubject.create<Boolean>()

        init {
            val formData = combineLatest<String, String, Pair<String, String>>(
                confirmationCodeTextChanged,passwordChanged, BiFunction { t1, t2 ->
                    Pair(t1, t2) }
            )
            formData
                .map { ValidationService.validateConfirmForgotPassword(it.first, it.second) }
                .subscribe(this.nextButtonEnabled)


            passwordChanged
                .map {validatePasswordLenght(it)}
                .subscribe(this.validPasswordLenght)

            passwordChanged
                .map { validatePasswordNumbers(it)}
                .subscribe(this.validPasswordNumbers)

            passwordChanged
                .map { validatePasswordCapitalLowerLetters(it)}
                .subscribe(this.validPasswordCapitalLowerLetters)

            passwordChanged
                .map { validatePasswordSpecialCharacters(it)}
                .subscribe(this.validPasswordSpecialCharacters)

            val confirmEvent = formData
                .takeWhen(this.nextButtonPressed)
                .flatMap { this.confirmForgotPassword(username,it.second.first, it.second.second) }
                .share()

            confirmEvent
                .filter { it.isFail() }
                .map { it ->
                    when(it){
                        is Result.failure -> return@map it.cause as ForgotPasswordError
                        is Result.success -> return@map null
                    }
                }
                .filter { it!= null }
                .map { ErrorHandler.handleError(it , AuthenticationErrorType.FORGOT_PASSWORD_ERROR) }
                .subscribe(this.showError)

            confirmEvent
                .filter { !it.isFail() }
                .map { it ->
                    when(it){
                        is Result.success -> return@map it.value as ForgotPasswordResult
                        is Result.failure -> return@map null
                    }
                }
                .filter { it!= null }
                .map{ it -> Unit}
                .subscribe(this.passwordChangedAction)
        }

        override fun passwordChanged(password: String) = this.passwordChanged.onNext(password)

        override fun confirmationCodeTextChanged(verificationCode: String) = this.confirmationCodeTextChanged.onNext(verificationCode)

        override fun nextButtonPressed() = this.nextButtonPressed.onNext(Unit)

        override fun nextButtonEnabled(): Observable<Boolean> = this.nextButtonEnabled

        override fun passwordChangedAction(): Observable<Unit> = this.passwordChangedAction

        override fun showError(): Observable<ErrorWrapper> = this.showError

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun validPasswordLenght(): Observable<Boolean> = this.validPasswordLenght

        override fun validPasswordCapitalLowerLetters(): Observable<Boolean> = this.validPasswordCapitalLowerLetters

        override fun validPasswordNumbers(): Observable<Boolean> = this.validPasswordNumbers

        override fun validPasswordSpecialCharacters(): Observable<Boolean> = this.validPasswordSpecialCharacters

        private fun confirmForgotPassword(username: String, confirmationCode: String, newPassword: String) : Observable<Result<ForgotPasswordResult>>{
            return environment.authenticationUseCase().confirmForgotPassword(username,confirmationCode, newPassword)
                .doOnComplete { this.loadingEnabled.onNext(false) }
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

        private fun validatePasswordLenght(password : String) : Boolean{
            val minPasswordLenght = 8
            val maxPasswordLenght = 20
            if(password.length < minPasswordLenght || password.length > maxPasswordLenght){
                return false
            }
            return true
        }

        private fun validatePasswordCapitalLowerLetters(password: String) : Boolean {
            val pattern1 = Pattern.compile(".*[A-Z].*")
            val pattern1Sub = Pattern.compile(".*[a-z].*")
            if (pattern1.matcher(password).matches() && pattern1Sub.matcher(password).matches()){
                return true
            }
            return false
        }

        private fun validatePasswordNumbers(password: String): Boolean{
            val pattern2 = Pattern.compile(".*\\d.*")
            if(pattern2.matcher(password).matches()){
                return true
            }
            return false
        }

        private fun validatePasswordSpecialCharacters(password: String) : Boolean {
            val pattern3 = Pattern.compile(".*[!\$#@_.+-].*")
            if(pattern3.matcher(password).matches()){
                return true
            }
            return false
        }
    }
}