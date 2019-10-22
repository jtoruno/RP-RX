package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ValidationService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface CompleteEmailVM {
    interface Inputs {
        /// Call when the email changed.
        fun emailChanged(email: String)

        /// Call when verify email is pressed
        fun verifyEmailPressed()
    }
    interface Outputs{
        // Emits a boolean indicating if the current email is valid
        fun isEmailValid() : Observable<Boolean>

        /// Emits a boolean indicating if the update email operation is being procced.
        fun loading() : Observable<Boolean>

        /// Emits an email string when should transition to confirm email.
        fun emailSuccessfullyConfirmed() : Observable<String>

        /// Emits an AlertError to be displayed when the update email operation failed
        fun showError() : Observable<String>

    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<CompleteEmailVM>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this
        private val resources = RPApplication.applicationContext().resources

        //Inputs
        private val emailChanged = PublishSubject.create<String>()
        private val verifyEmailPressed = PublishSubject.create<Unit>()
        //Outputs
        private val isEmailValid = BehaviorSubject.create<Boolean>()
        private val loading = BehaviorSubject.create<Boolean>()
        private val emailSuccessfullyConfirmed = BehaviorSubject.create<String>()
        private val showError = BehaviorSubject.create<String>()

        init {
            emailChanged
                .map { ValidationService.isValidEmail(it) }
                .subscribe(this.isEmailValid)

            val event = emailChanged
                .takeWhen(this.verifyEmailPressed)
                .flatMap { this.updateAndVerifyEmail(it.second)}
                .share()

            event
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.emailSuccessfullyConfirmed)

            event
                .filter { it.isFail() }
                .map { return@map resources.getString(R.string.Error_verifying_user) }
                .subscribe(this.showError)

        }


        override fun emailChanged(email: String) {
            return this.emailChanged.onNext(email)
        }

        override fun verifyEmailPressed() {
            return this.verifyEmailPressed.onNext(Unit)
        }

        override fun isEmailValid(): Observable<Boolean> = this.isEmailValid

        override fun loading(): Observable<Boolean> = this.loading

        override fun emailSuccessfullyConfirmed(): Observable<String> = this.emailSuccessfullyConfirmed

        override fun showError(): Observable<String> = this.showError

        private fun updateAndVerifyEmail(email: String) : Observable<Result<String>>{
            val single = Single.create<Result<String>> create@{ single ->
                val attributes = mutableMapOf<String,String>()
                attributes["email"] = email
                val updateEmail = environment.authenticationUseCase().updateUserAttributes(attributes)
                    .share()

                updateEmail
                    .filter { it.isFail() }
                    .map {
                        when(it){
                            is Result.failure -> return@map it.cause
                            is Result.success -> return@map null
                        }
                    }
                    .subscribe {
                        single.onSuccess(Result.failure(it))
                    }
                val verifyEvent = updateEmail
                    .filter { !it.isFail() }
                    .map { it.successValue() }
                    .flatMap { environment.authenticationUseCase().verifyEmail() }
                    .share()
                verifyEvent
                    .filter { it.isFail() }
                    .map {
                        when(it){
                            is Result.failure -> return@map it.cause
                            is Result.success -> return@map null
                        }
                    }
                    .subscribe {
                        single.onSuccess(Result.failure(it))
                    }
                verifyEvent
                    .filter { !it.isFail() }
                    .map { it.successValue() }
                    .subscribe {
                        single.onSuccess(Result.success(it))
                    }
            }
            return single.toObservable()
                .doOnComplete { this.loading.onNext(false) }
                .doOnSubscribe { this.loading.onNext(true) }
        }


    }
}