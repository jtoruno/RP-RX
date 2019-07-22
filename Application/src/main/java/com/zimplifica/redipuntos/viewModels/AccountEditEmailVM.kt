package com.zimplifica.redipuntos.viewModels

import android.annotation.SuppressLint
import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ValidationService
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.lang.Exception

interface AccountEditEmailVM {
    interface Inputs {
        /// Call when the email is changed.
        fun emailValueChanged(email: String)

        /// Call when the verify button is pressed.
        fun verifyEmailPressed()

        fun onCreated()
    }
    interface Outputs{
        /// Emits when the email change is triggered.
        fun verifyButtonEnabled() : Observable<Boolean>

        /// Emits when the email change button is triggered.
        fun verifyEmailAction() : Observable<String>

        /// Emits when the user information action is triggered.
        fun emailAction() : Observable<String>

        /// Emits when the request is being processed.
        fun loadingEnabled() : Observable<Boolean>

        /// Emits an AlertError to be displayed when the update email operation failed
        fun showError() : Observable<String>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<AccountEditEmailVM>(environment),Inputs,Outputs{


        val inputs : Inputs = this
        val outputs : Outputs = this
        private var emailValue: String = ""

        //Inputs
        private val emailValueChanged = PublishSubject.create<String>()
        private val verifyEmailPressed = PublishSubject.create<Unit>()
        private val onCreated = PublishSubject.create<Unit>()

        //Outputs
        private val verifyButtonEnabled = BehaviorSubject.create<Boolean>()
        private val verifyEmailAction = BehaviorSubject.create<String>()
        private val emailAction = BehaviorSubject.create<String>()
        private val loadingEnabled = BehaviorSubject.create<Boolean>()
        private val showError = BehaviorSubject.create<String>()

        init {
            this.onCreated
                .map { environment.currentUser().getCurrentUser() }
                .map { it.userEmail?:"" }
                .subscribe(this.emailAction)

            environment.userUseCase().getUserInformationSubscription()
                .subscribe {
                    val email = it.userEmail
                    this.emailAction.onNext(email?:"")
                    if (email != null){
                        this.emailValueChanged(email)
                    }
                }

            this.emailValueChanged
                .map { this.validateEmailScenarios(it) }
                .subscribe(this.verifyButtonEnabled)

            val event = this.emailValueChanged
                .filter { this.validateEmailScenarios(it) }
                .takeWhen(this.verifyEmailPressed)
                .flatMap {
                    this.handleUpdateEmailAndVerifyEmail(it.second)
                }
                .share()

            event
                .filter { !it.isFail() }
                .map { it.successValue() }
                .map {
                    val email = this.emailValue
                    return@map email
                }
                .subscribe(this.verifyEmailAction)

            event
                .filter { it.isFail() }
                .map {
                    when(it){
                        is Result.failure -> return@map it.cause
                        else -> return@map null
                    }
                }
                .map { it.message ?: "" }
                .subscribe(this.showError)
        }

        override fun onCreated() {
            return this.onCreated.onNext(Unit)
        }

        override fun emailValueChanged(email: String) {
            return this.emailValueChanged.onNext(email)
        }

        override fun verifyEmailPressed() {
            return this.verifyEmailPressed.onNext(Unit)
        }

        override fun verifyButtonEnabled(): Observable<Boolean> = this.verifyButtonEnabled

        override fun verifyEmailAction(): Observable<String> = this.verifyEmailAction

        override fun emailAction(): Observable<String> = this.emailAction

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun showError(): Observable<String> = this.showError

        private fun updateEmail(email: String) : Observable<Result<Boolean>>{
            val map = mutableMapOf<String,String>()
            map["email"] = email
            return environment.authenticationUseCase().updateUserAttributes(map)
        }

        private fun sendVerificationCode() : Observable<Result<String>>{
            return environment.authenticationUseCase().verifyEmail()
        }

        private fun validateEmailScenarios(email: String) : Boolean{
            if(!ValidationService.isValidEmail(email)){return false}
            val userEmailVerified = environment.currentUser().getCurrentUser()?.userEmailVerified ?: return false
            if (!userEmailVerified){
                emailValue = email
                return true
            }

            if (environment.currentUser().getCurrentUser()?.userEmail == email){return false}
            emailValue = email
            return true
        }

        private fun handleUpdateEmailAndVerifyEmail(email: String) : Observable<Result<String>>{
            return Observable.create<Result<String>> { disposable ->

                val updateEmailEvent = this.updateEmail(email)
                    .share()
                updateEmailEvent
                    .filter { it.isFail() }
                    .subscribe {
                        val error = Exception("Ocurrió un error al actualizar el email. Por favor intenta de nuevo.")
                        disposable.onNext(Result.failure(error))
                        disposable.onComplete()
                    }
                val verifyEvent = updateEmailEvent
                    .filter { !it.isFail() }
                    .map { it.successValue() }
                    .flatMap { return@flatMap this.sendVerificationCode() }
                    .share()

                verifyEvent
                    .filter { !it.isFail() }
                    .map { it.successValue() }
                    .subscribe {
                        disposable.onNext(Result.success(it))
                        disposable.onComplete()
                    }
                verifyEvent
                    .filter { it.isFail() }
                    .subscribe {
                        val error = Exception("Ocurrió un error al verificar el usuario. Por favor intenta de nuevo.")
                        disposable.onNext(Result.failure(error))
                        disposable.onComplete()
                    }
            }.doOnComplete { this.loadingEnabled.onNext(false) }.doOnSubscribe { this.loadingEnabled.onNext(true) }
        }
    }
}