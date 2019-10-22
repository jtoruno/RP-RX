package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface ConfirmEmailVM {
    interface Inputs {
        fun verificationCodeChanged(code: String)
        fun confirmEmailButtonPressed()
        fun resendCodeButtonPressed()
        fun confirmResendButtonPressed()
    }
    interface Outputs {
        fun email() : Observable<String>
        fun loading() : Observable<Boolean>
        fun confirmedEmail() : Observable<Unit>
        fun showError() : Observable<String>
        fun codeResent() : Observable<Unit>
        fun isButtonEnabled() : Observable<Boolean>
        fun showResendAlert () : Observable <Unit>

    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<ConfirmEmailVM>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this
        private val resources = RPApplication.applicationContext().resources

        //Inputs
        private val verificationCodeChanged = PublishSubject.create<String>()
        private val confirmEmailButtonPressed = PublishSubject.create<Unit>()
        private val resendCodeButtonPressed = PublishSubject.create<Unit>()
        private val confirmResendButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val email = BehaviorSubject.create<String>()
        private val loading = BehaviorSubject.create<Boolean>()
        private val confirmedEmail = BehaviorSubject.create<Unit>()
        private val showError = BehaviorSubject.create<String>()
        private val codeResent = BehaviorSubject.create<Unit>()
        private val isButtonEnabled = BehaviorSubject.create<Boolean>()
        private val showResendAlert = BehaviorSubject.create<Unit>()

        init {
            resendCodeButtonPressed
                .subscribe(this.showResendAlert)

            intent()
                .filter { it.hasExtra("email") }
                .map { it.getStringExtra("email") }
                .subscribe(this.email)

            verificationCodeChanged
                .map { return@map it.length == 6 }
                .subscribe(this.isButtonEnabled)

            val confirmEvent = verificationCodeChanged
                .takeWhen(this.confirmEmailButtonPressed)
                .flatMap { this.confirmEmail(it.second) }
                .share()

            confirmEvent
                .filter { !it.isFail() }
                .map { Unit }
                .subscribe(this.confirmedEmail)

            val confirmationError = confirmEvent
                .filter { it.isFail() }
                .map { return@map resources.getString(R.string.Error_confirm_email) }


            val resendEvent = confirmResendButtonPressed
                .flatMap { this.verifyEmail() }
                .share()

            resendEvent
                .filter { !it.isFail() }
                .map { Unit }
                .subscribe(this.codeResent)

            val resendError = resendEvent
                .filter { it.isFail() }
                .map { resources.getString(R.string.Error_confirm_email) }

            Observable.merge(resendError,confirmationError)
                .subscribe(this.showError)
        }


        override fun verificationCodeChanged(code: String) = this.verificationCodeChanged.onNext(code)

        override fun confirmEmailButtonPressed() = this.confirmEmailButtonPressed.onNext(Unit)

        override fun resendCodeButtonPressed() = this.resendCodeButtonPressed.onNext(Unit)

        override fun confirmResendButtonPressed() = this.confirmResendButtonPressed.onNext(Unit)

        override fun email(): Observable<String> = this.email

        override fun loading(): Observable<Boolean> = this.loading

        override fun confirmedEmail(): Observable<Unit> = this.confirmedEmail

        override fun showError(): Observable<String> = this.showError

        override fun codeResent(): Observable<Unit> = this.codeResent

        override fun isButtonEnabled(): Observable<Boolean> = this.isButtonEnabled

        override fun showResendAlert(): Observable<Unit> = this.showResendAlert

        private fun confirmEmail(code : String) : Observable<Result<Boolean>>{
            return environment.authenticationUseCase().confirmEmail(code)
                .doOnComplete { this.loading.onNext(false) }
                .doOnSubscribe { this.loading.onNext(true) }
        }

        private fun verifyEmail() : Observable<Result<String>>{
            return environment.authenticationUseCase().verifyEmail()
                .doOnComplete { this.loading.onNext(false) }
                .doOnSubscribe { this.loading.onNext(true) }
        }

    }
}