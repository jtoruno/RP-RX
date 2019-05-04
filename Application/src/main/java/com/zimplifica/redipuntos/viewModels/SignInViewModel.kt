package com.zimplifica.redipuntos.viewModels

import android.content.Context
import android.support.annotation.NonNull
import android.util.Log
import com.zimplifica.domain.entities.SignInError
import com.zimplifica.domain.entities.SignInResult
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.UserCredentialType
import com.zimplifica.redipuntos.libs.utils.ValidationService
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface SignInViewModel {
    interface Inputs {
        fun username(username: String)
        fun password(password: String)
        fun signInButtonPressed()

    }
    interface Outputs {
        fun signInButtonIsEnabled() : Observable<Boolean>
        fun print() : Observable<String>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<SignInViewModel>(environment), Inputs, Outputs{

        //Inputs
        val s : String = environment.webEndpoint()
        private val usernameEditTextChanged =  PublishSubject.create<String>()
        private val passwordEditTextChanged =  PublishSubject.create<String>()
        private val signInButtonPressed = PublishSubject.create<Unit>()


        //Outputs
        private val signInButtonIsEnabled = BehaviorSubject.create<Boolean>()

        val inputs : Inputs = this
        val outputs : Outputs = this

        init {
            val usernameAndPassword = combineLatest<String, String, Pair<String, String>>(usernameEditTextChanged,passwordEditTextChanged,
                BiFunction { t1, t2 ->
                    Pair(t1, t2)
                })
            val valid = usernameAndPassword.map {
                ValidationService.validateUserCredentials(it.first, it.second)
            }
            valid.subscribe(this.signInButtonIsEnabled)

            val signInEvent = usernameAndPassword
                .takeWhen(signInButtonPressed)
                .flatMap { result ->
                    return@flatMap this.signIn(result.second.first, result.second.second)
                }
                .share()
            /*
            signInEvent
                .map {
                    if (it.isFailure){
                        return@map it.exceptionOrNull()
                    }else{
                        return@map null
                    }
                }
                .filter({it !=null})
             */

        }
        override fun username(username: String) = this.usernameEditTextChanged.onNext(username)

        override fun password(password: String) = this.passwordEditTextChanged.onNext(password)

        override fun signInButtonPressed() = this.signInButtonPressed.onNext(Unit)

        override fun signInButtonIsEnabled(): Observable<Boolean> = this.signInButtonIsEnabled

        override fun print(): Observable<String> {
            return Observable.just(s)
        }

        private fun signIn(username: String, password: String) : Observable<Result<SignInResult>>{
            var usernameFormated = ""
            val credentialType = ValidationService.userCredentialType(username)
            usernameFormated = when(credentialType){
                UserCredentialType.PHONE_NUMBER -> "+506$username"
                UserCredentialType.EMAIL -> username
            }
            return environment.authenticationUseCase().signIn(usernameFormated, password)
        }

        private  fun isValid (username: String, password: String) = username.isNotEmpty() && password.isNotEmpty()

    }
}