package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.SignUpError
import com.zimplifica.domain.entities.SignUpResult
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.regex.Pattern

interface PasswordViewModel {
    interface Inputs {
        fun password(password : String)
        fun termsAndConditionsButtonPressed()
        fun privacyPolicyButtonPressed()
        fun signUpButtonPressed()

    }
    interface Outputs {
        fun signUpButtonEnabled() : Observable<Boolean>
        fun startTermsActivity() : Observable<Unit>
        fun startPolicyActivity() : Observable<Unit>

        fun validPasswordLenght() : Observable<Boolean>
        fun validPasswordCapitalLowerLetters() : Observable<Boolean>
        fun validPasswordNumbers() : Observable<Boolean>
        fun validPasswordSpecialCharacters() : Observable<Boolean>
    }

    class ViewModel(@NonNull val environment: Environment):ActivityViewModel<PasswordViewModel>(environment),Inputs,Outputs{


        val inputs : Inputs = this
        val outputs : Outputs = this

        val authUseCase = environment.authenticationUseCase()

        //Inputs
        private val passwordEditTextChanged = PublishSubject.create<String>()
        private val termsButtonPressed = PublishSubject.create<Unit>()
        private val privacyButtonPressed = PublishSubject.create<Unit>()
        private val signUpButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val startTermsActivity : Observable<Unit>
        private val startPolicyActivity : Observable<Unit>
        private val signUpButtonEnabled = BehaviorSubject.create<Boolean>()
        private val validPasswordCapitalLowerLetters = BehaviorSubject.create<Boolean>()
        private val validPasswordLenght = BehaviorSubject.create<Boolean>()
        private val validPasswordNumbers = BehaviorSubject.create<Boolean>()
        private val validPasswordSpecialCharacters = BehaviorSubject.create<Boolean>()

        init {
            this.startTermsActivity = this.termsButtonPressed
            this.startPolicyActivity = this.privacyButtonPressed
            val validPassword = passwordEditTextChanged
                .map { validatePassword(it) }
            validPassword.subscribe(signUpButtonEnabled)

            passwordEditTextChanged
                .map {validatePasswordLenght(it)}
                .subscribe(this.validPasswordLenght)

            passwordEditTextChanged
                .map { validatePasswordNumbers(it)}
                .subscribe(this.validPasswordNumbers)

            passwordEditTextChanged
                .map { validatePasswordCapitalLowerLetters(it)}
                .subscribe(this.validPasswordCapitalLowerLetters)

            passwordEditTextChanged
                .map { validatePasswordSpecialCharacters(it)}
                .subscribe(this.validPasswordSpecialCharacters)

            /*
            val signUpEvent =passwordEditTextChanged
                .takeWhen(this.signUpButtonPressed)
                .switchMap{it -> this.submit(UUID.randomUUID().toString(),"+50689626004",it.second)}

            */


        }

        override fun signUpButtonPressed() = this.signUpButtonPressed.onNext(Unit)

        override fun password(password: String) = this.passwordEditTextChanged.onNext(password)

        override fun termsAndConditionsButtonPressed() = this.termsButtonPressed.onNext(Unit)

        override fun privacyPolicyButtonPressed() = this.privacyButtonPressed.onNext(Unit)

        override fun signUpButtonEnabled(): Observable<Boolean> = this.signUpButtonEnabled

        override fun startTermsActivity(): Observable<Unit> = this.startTermsActivity

        override fun startPolicyActivity(): Observable<Unit> = this.startPolicyActivity

        override fun validPasswordLenght(): Observable<Boolean> = this.validPasswordLenght

        override fun validPasswordCapitalLowerLetters(): Observable<Boolean> = this.validPasswordCapitalLowerLetters

        override fun validPasswordNumbers(): Observable<Boolean> = this.validPasswordNumbers

        override fun validPasswordSpecialCharacters(): Observable<Boolean> = this.validPasswordSpecialCharacters


        private fun submit(uuid: String,username : String, password: String) : Observable<Result<SignUpResult>> {
            return this.authUseCase.signUp(uuid, username, password)

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

        private fun validatePassword(password: String) : Boolean {
            var isSuccess = true
            if (!validatePasswordLenght(password)){isSuccess = false}
            if (!validatePasswordCapitalLowerLetters(password)){isSuccess = false}
            if (!validatePasswordNumbers(password)){isSuccess = false}
            if (!validatePasswordSpecialCharacters(password)){isSuccess = false}

            return isSuccess
        }

    }
}