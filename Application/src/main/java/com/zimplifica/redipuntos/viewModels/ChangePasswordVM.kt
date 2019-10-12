package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.ChangePasswordModel
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ValidationService
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.regex.Pattern

interface ChangePasswordVM {
    interface Inputs {
        //On Create
        fun onCreate()

        /// Call when value of the verification code textfield changes.
        fun verificationCodeChange(verificationCode: String)

        /// Call when value of the new password textfield changes.
        fun newPasswordChanged(password: String)

        /// Call when 'change password' button is pressed.
        fun changePasswordButtonPressed()
    }
    interface Outputs {
        /// Emits a boolean that determinates if the change password button should be enabled or not.
        fun changePasswordButtonEnabled() : Observable<Boolean>
        /// Emits a when the change password is triggered.
        fun changePasswordAction() : Observable<Unit>
        // Emits when an error is triggered and a message should be displayed.
        fun showError() : Observable<String>

        /// Emits when user tries to change the password.
        fun loadingEnabled() : Observable<Boolean>

        fun validPasswordLength() : Observable<Boolean>
        fun validPasswordCapitalLowerLetters() : Observable<Boolean>
        fun validPasswordNumbers() : Observable<Boolean>
        fun validPasswordSpecialCharacters() : Observable<Boolean>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<ChangePasswordVM>(environment),Inputs, Outputs{



        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()
        private val verificationCodeChange = PublishSubject.create<String>()
        private val newPasswordChanged = PublishSubject.create<String>()
        private val changePasswordButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val changePasswordAction = BehaviorSubject.create<Unit>()
        private val showError = BehaviorSubject.create<String>()
        private val loadingEnabled = BehaviorSubject.create<Boolean>()

        private val validPasswordLength = BehaviorSubject.create<Boolean>()
        private val validPasswordCapitalLowerLetters = BehaviorSubject.create<Boolean>()
        private val validPasswordNumbers = BehaviorSubject.create<Boolean>()
        private val validPasswordSpecialCharacters = BehaviorSubject.create<Boolean>()
        private val changePasswordButtonEnabled = BehaviorSubject.create<Boolean>()

        init {

            val verifyPhoneEvent = this.onCreate
                .flatMap { return@flatMap this.verifyPhoneNumber() }
                .share()

            verifyPhoneEvent
                .filter { !it.isFail() }
                .subscribe{Unit}

            val validPasswordForm = Observables.combineLatest(
                this.verificationCodeChange.map { code -> ValidationService.validateVerificationCode(code) },
                this.newPasswordChanged.map { password -> validatePassword(password) })

            validPasswordForm
                .map { return@map it.first == true && it.second == true }
                .subscribe(this.changePasswordButtonEnabled)


            val form = Observables.combineLatest(
                verificationCodeChange, newPasswordChanged
            )

            newPasswordChanged
                .map {validatePasswordLength(it)}
                .subscribe(this.validPasswordLength)

            newPasswordChanged
                .map { validatePasswordNumbers(it)}
                .subscribe(this.validPasswordNumbers)

            newPasswordChanged
                .map { validatePasswordCapitalLowerLetters(it)}
                .subscribe(this.validPasswordCapitalLowerLetters)

            newPasswordChanged
                .map { validatePasswordSpecialCharacters(it)}
                .subscribe(this.validPasswordSpecialCharacters)

            val event = form
                .takeWhen(this.changePasswordButtonPressed)
                .flatMap { return@flatMap changePassword(it.second.first, it.second.second) }
                .share()

            Observable.merge(verifyPhoneEvent,event)
                .filter { it.isFail() }
                .map { return@map "Ocurrió un error al cambiar la contraseña. Por favor intente de nuevo." }
                .subscribe(this.showError)


            event
                .filter { !it.isFail() }
                .map { Unit }
                .subscribe(this.changePasswordAction)

        }

        override fun onCreate() {
            return this.onCreate.onNext(Unit)
        }

        override fun verificationCodeChange(verificationCode: String) {
            return this.verificationCodeChange.onNext(verificationCode)
        }

        override fun newPasswordChanged(password: String) {
            return this.newPasswordChanged.onNext(password)
        }

        override fun changePasswordButtonPressed() {
            return this.changePasswordButtonPressed.onNext(Unit)
        }

        override fun changePasswordAction(): Observable<Unit> = this.changePasswordAction

        override fun showError(): Observable<String> = this.showError

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun validPasswordLength(): Observable<Boolean> = this.validPasswordLength

        override fun validPasswordCapitalLowerLetters(): Observable<Boolean> = this.validPasswordCapitalLowerLetters

        override fun validPasswordNumbers(): Observable<Boolean> = this.validPasswordNumbers

        override fun validPasswordSpecialCharacters(): Observable<Boolean> = this.validPasswordSpecialCharacters

        override fun changePasswordButtonEnabled(): Observable<Boolean> = changePasswordButtonEnabled


        //Helpers
        private fun validatePasswordLength(password : String) : Boolean{
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

        private fun validatePassword(password: String) : Boolean {
            var isSuccess = true
            if (!validatePasswordLength(password)){isSuccess = false}
            if (!validatePasswordCapitalLowerLetters(password)){isSuccess = false}
            if (!validatePasswordNumbers(password)){isSuccess = false}
            if (!validatePasswordSpecialCharacters(password)){isSuccess = false}

            return isSuccess
        }


        private fun changePassword(verificationCode: String, password: String) : Observable<Result<Boolean>> {
            val changePasswordModel = ChangePasswordModel( verificationCode,  password)
            return environment.userUseCase().changePassword(changePasswordModel)
                .doOnComplete { this.loadingEnabled.onNext(false) }
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

        private fun verifyPhoneNumber() : Observable<Result<Boolean>>  {
            return environment.userUseCase().verifyPhoneNumber()
                //.doOnComplete { this.loadingEnabled.onNext(false) }
                //.doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

    }
}