package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import android.support.annotation.VisibleForTesting
import android.util.Log
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
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.libs.utils.*
import com.zimplifica.redipuntos.models.SignUpModel
import io.reactivex.functions.BiFunction

interface PasswordViewModel {
    interface Inputs {
        fun password(password : String)
        fun termsAndConditionsButtonPressed()
        fun privacyPolicyButtonPressed()
        fun signUpButtonPressed()
        //fun username(username: String)

    }
    interface Outputs {
        fun signUpButtonEnabled() : Observable<Boolean>
        fun startTermsActivity() : Observable<Unit>
        fun startPolicyActivity() : Observable<Unit>

        fun validPasswordLenght() : Observable<Boolean>
        fun validPasswordCapitalLowerLetters() : Observable<Boolean>
        fun validPasswordNumbers() : Observable<Boolean>
        fun validPasswordSpecialCharacters() : Observable<Boolean>

        fun verifyPhoneNumberAction() : Observable<SignUpModel>
        fun showError() : Observable<ErrorWrapper>
        fun loadingEnabled() : Observable<Boolean>
    }

    class ViewModel(@NonNull val environment: Environment):ActivityViewModel<PasswordViewModel>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        private val uuid = UUID.randomUUID().toString()
        private var phone : String? = null

        //Inputs
        private val passwordEditTextChanged = BehaviorSubject.create<String>()
        private val termsButtonPressed = PublishSubject.create<Unit>()
        private val privacyButtonPressed = PublishSubject.create<Unit>()
        private val signUpButtonPressed = PublishSubject.create<Unit>()
        //private val username = BehaviorSubject.create<String>()

        //Outputs
        private val startTermsActivity : Observable<Unit>
        private val startPolicyActivity : Observable<Unit>
        private val signUpButtonEnabled = BehaviorSubject.create<Boolean>()
        private val validPasswordCapitalLowerLetters = BehaviorSubject.create<Boolean>()
        private val validPasswordLenght = BehaviorSubject.create<Boolean>()
        private val validPasswordNumbers = BehaviorSubject.create<Boolean>()
        private val validPasswordSpecialCharacters = BehaviorSubject.create<Boolean>()

        private val loadingEnabled = BehaviorSubject.create<Boolean>()
        private val showError = BehaviorSubject.create<ErrorWrapper>()
        private val verifyPhoneNumberAction = PublishSubject.create<SignUpModel>()

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

            val phone  = intent()
                .filter { it.hasExtra("phone") }
                .map {
                    this.phone = it.getStringExtra("phone")
                    Log.e("PSVM","PhoneNumber "+phone)
                    return@map it.getStringExtra("phone")
                }


            val usernameAndPassword = Observable.combineLatest<String, String, Pair<String, String>>(
                phone,
                passwordEditTextChanged,
                BiFunction { t1, t2 ->
                    Pair(t1, t2)
                })

            val verifyPhoneNumber = usernameAndPassword
                .takeWhen(this.signUpButtonPressed)
                .flatMap{it -> this.verifyPhoneNumber(uuid,it.second.first, it.second.second)}
                .share()

            verifyPhoneNumber
                .filter { it.isFail() }
                .map { it ->
                    when(it){
                        is Result.success -> return@map null
                        is Result.failure -> return@map it.cause as? SignUpError
                    } }
                .filter { it!= null }
                .map { ErrorHandler.handleError(it , AuthenticationErrorType.SIGN_UP_ERROR) }
                .subscribe(this.showError)

            verifyPhoneNumber
                .filter { !it.isFail() }
                .map{it ->
                    when(it){
                        is Result.success -> return@map it.value
                        is Result.failure -> return@map null
                    }}
                .map { result ->
                    val password = this.passwordEditTextChanged.value
                    val username = this.phone ?:""
                    print(username+ password+uuid+"\n")
                    environment.sharedPreferences().edit().putString("phoneNumber",username).apply()
                    environment.sharedPreferences().edit().putString("userId",uuid).apply()
                    environment.sharedPreferences().edit().putString("password",password).apply()
                    return@map SignUpModel(uuid,username,password)
                }
                .filter { it!=null }
                .subscribe(this.verifyPhoneNumberAction)

        }

        override fun signUpButtonPressed() = this.signUpButtonPressed.onNext(Unit)

        override fun password(password: String) = this.passwordEditTextChanged.onNext(password)

        override fun termsAndConditionsButtonPressed() = this.termsButtonPressed.onNext(Unit)

        override fun privacyPolicyButtonPressed() = this.privacyButtonPressed.onNext(Unit)
        /*
        override fun username(username: String) {
            return this.username.onNext(username)
        }*/

        override fun signUpButtonEnabled(): Observable<Boolean> = this.signUpButtonEnabled

        override fun startTermsActivity(): Observable<Unit> = this.startTermsActivity

        override fun startPolicyActivity(): Observable<Unit> = this.startPolicyActivity

        override fun validPasswordLenght(): Observable<Boolean> = this.validPasswordLenght

        override fun validPasswordCapitalLowerLetters(): Observable<Boolean> = this.validPasswordCapitalLowerLetters

        override fun validPasswordNumbers(): Observable<Boolean> = this.validPasswordNumbers

        override fun validPasswordSpecialCharacters(): Observable<Boolean> = this.validPasswordSpecialCharacters

        override fun verifyPhoneNumberAction(): Observable<SignUpModel> = this.verifyPhoneNumberAction

        override fun showError(): Observable<ErrorWrapper> = this.showError

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled


        private fun verifyPhoneNumber(uuid: String,username : String, password: String) : Observable<Result<Boolean>> {
            /*
            var usernameFormated = ""
            val credentialType = ValidationService.userCredentialType(username)
            usernameFormated = when(credentialType){
                UserCredentialType.PHONE_NUMBER -> "+506$username"
                UserCredentialType.EMAIL -> username
            }*/
            return environment.authenticationUseCase().verifyPhoneNumber(username)
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

        private fun validatePassword(password: String) : Boolean {
            var isSuccess = true
            if (!validatePasswordLenght(password)){isSuccess = false}
            if (!validatePasswordCapitalLowerLetters(password)){isSuccess = false}
            if (!validatePasswordNumbers(password)){isSuccess = false}
            if (!validatePasswordSpecialCharacters(password)){isSuccess = false}

            return isSuccess
        }
        @VisibleForTesting
        internal fun getUuid() : String{
            return this.uuid
        }

    }
}