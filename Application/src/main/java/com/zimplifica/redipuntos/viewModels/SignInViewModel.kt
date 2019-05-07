package com.zimplifica.redipuntos.viewModels

import android.content.Context
import android.support.annotation.NonNull
import android.util.Log
import com.zimplifica.domain.entities.SignInError
import com.zimplifica.domain.entities.SignInResult
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.*
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import rx.Subscriber
import rx.subscriptions.BooleanSubscription
import java.lang.Exception
import com.zimplifica.domain.entities.Result

interface SignInViewModel {
    interface Inputs {
        fun username(username: String)
        fun password(password: String)
        fun signInButtonPressed()

    }
    interface Outputs {
        fun loadingEnabled(): Observable<Boolean>
        fun showError() : Observable<ErrorWrapper>
        fun signInButtonIsEnabled() : Observable<Boolean>
        fun signedInAction(): Observable<Unit>
        fun showConfirmationAlert() : Observable<Unit>
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
        private val loadingEnabled = BehaviorSubject.create<Boolean>()
        private val showError = PublishSubject.create<ErrorWrapper>()
        private val showConfirmationAlert = PublishSubject.create<Unit>()
        private val signedInAction = PublishSubject.create<Unit>()


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

            val signInEvent : Observable<Result<SignInResult>> = usernameAndPassword
                .takeWhen(signInButtonPressed)
                .flatMap { result ->
                    return@flatMap this.signIn(result.second.first, result.second.second)
                }
                .share()


//            signInEvent.subscribe(object: Observer<Result<SignInResult>>{
//                override fun onComplete() {
//
//                }
//
//                override fun onSubscribe(d: Disposable) {
//                }
//
//                override fun onNext(t: Result<SignInResult>) {
//                    print("\n Is Failure"+t.isFailure)
//                }
//
//                override fun onError(e: Throwable) {
//
//                }
//
//            })

            signInEvent
                .filter { it.isFail() }
                .map { it ->
                    when(it){
                        is Result.success -> return@map null
                        is Result.failure -> return@map it.cause as SignInError?
                    }
                }
                .map {ErrorHandler.handleError(it , AuthenticationErrorType.SIGN_IN_ERROR)}
                .subscribe(this.showError)
                    /*
                //.filter {it.exceptionOrNull() != null}
                .map {
                    return@map it.exceptionOrNull()
                }
                .map {ErrorHandler.handleError(it as Exception, AuthenticationErrorType.SIGN_IN_ERROR)}
                .subscribe(this.showError)
                */

            showError
                .filter { it ->
                    ((it.error) as? SignInError).let {
                        when(it){
                            SignInError.userNotConfirmed-> return@filter true
                            else -> return@filter false
                        }
                    }
                }
                .map{ it -> Unit}
                .subscribe(this.showConfirmationAlert)

           signInEvent
               .filter { !it.isFail() }
                .map { it ->
                    when(it){
                        is Result.success -> return@map it.value
                        is Result.failure -> return@map null
                    }
                }
               .filter { it!= null }
                .map { it -> Unit }
                .subscribe(this.signedInAction)




        }
        override fun username(username: String) = this.usernameEditTextChanged.onNext(username)

        override fun password(password: String) = this.passwordEditTextChanged.onNext(password)

        override fun signInButtonPressed() = this.signInButtonPressed.onNext(Unit)

        override fun signInButtonIsEnabled(): Observable<Boolean> = this.signInButtonIsEnabled

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun showError(): Observable<ErrorWrapper> = this.showError

        override fun signedInAction(): Observable<Unit> = this.signedInAction

        override fun showConfirmationAlert(): Observable<Unit> = this.showConfirmationAlert

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
                .doOnComplete { this.loadingEnabled.onNext(false) }
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

        private  fun isValid (username: String, password: String) = username.isNotEmpty() && password.isNotEmpty()

    }
}