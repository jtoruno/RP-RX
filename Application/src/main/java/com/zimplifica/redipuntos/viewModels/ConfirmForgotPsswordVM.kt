package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.ForgotPasswordError
import com.zimplifica.domain.entities.ForgotPasswordModel
import com.zimplifica.domain.entities.ForgotPasswordResult
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
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
import io.reactivex.rxkotlin.Observables
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
        fun passwordChangedAction() : Observable<Boolean>
        fun showError() : Observable<String>
        fun loadingEnabled() : Observable<Boolean>

        fun validPasswordLenght() : Observable<Boolean>
        fun validPasswordCapitalLowerLetters() : Observable<Boolean>
        fun validPasswordNumbers() : Observable<Boolean>
        fun validPasswordSpecialCharacters() : Observable<Boolean>
    }

    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<ConfirmForgotPsswordVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this
        private val resources = RPApplication.applicationContext().resources

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
        private val passwordChangedAction = PublishSubject.create<Boolean>()
        private val showError = PublishSubject.create<String>()
        private val loadingEnabled = BehaviorSubject.create<Boolean>()

        init {
            val username = intent()
                .filter { it.hasExtra("username") }
                .map { return@map it.getStringExtra("username") }


            val formData = Observables.combineLatest(
                confirmationCodeTextChanged,passwordChanged)

            formData
                .map { ValidationService.validateConfirmForgotPassword(it.first,it.second) }
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

            val allInputsForm = Observables.combineLatest(formData,username)

            val confirmEvent = allInputsForm
                .takeWhen(this.nextButtonPressed)
                .flatMap { this.confirmForgotPassword(it.second.second,it.second.first.first,it.second.first.second) }
                .share()

            confirmEvent
                .filter { it.isFail() }
                .map { return@map  resources.getString(R.string.Error_forgot_password) }
                .subscribe(this.showError)

            confirmEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.passwordChangedAction)
        }

        override fun passwordChanged(password: String) = this.passwordChanged.onNext(password)

        override fun confirmationCodeTextChanged(verificationCode: String) = this.confirmationCodeTextChanged.onNext(verificationCode)

        override fun nextButtonPressed() = this.nextButtonPressed.onNext(Unit)

        override fun nextButtonEnabled(): Observable<Boolean> = this.nextButtonEnabled

        override fun passwordChangedAction(): Observable<Boolean> = this.passwordChangedAction

        override fun showError(): Observable<String> = this.showError

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun validPasswordLenght(): Observable<Boolean> = this.validPasswordLenght

        override fun validPasswordCapitalLowerLetters(): Observable<Boolean> = this.validPasswordCapitalLowerLetters

        override fun validPasswordNumbers(): Observable<Boolean> = this.validPasswordNumbers

        override fun validPasswordSpecialCharacters(): Observable<Boolean> = this.validPasswordSpecialCharacters

        private fun confirmForgotPassword(username: String, confirmationCode: String, newPassword: String) : Observable<Result<Boolean>>{
            val forgotPasswordModel = ForgotPasswordModel(confirmationCode,newPassword,username)
            return environment.userUseCase().confirmForgotPassword(forgotPasswordModel)
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