package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ValidationService
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface AccountVerifyEmailVM {
    interface Inputs{
        /// Call when the code is changed.
        fun codeValueChanged(code: String)

        /// Call when the verify button is pressed.
        fun verifyCodePressed()
    }
    interface Outputs{
        // Emits when the code change is triggered.
        fun validCode() : Observable<Boolean>

        /// Emits when the code change button is triggered.
        fun verifyCodeAction() : Observable<Unit>

        /// Emits when the request is being processed.
        fun loadingEnabled() : Observable<Boolean>

        /// Emits an AlertError to be displayed when the update email operation failed
        fun showError() : Observable<String>

        /// Emits when the email is changed.
        fun emailAction() : Observable<String>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<AccountVerifyEmailVM>(environment),Inputs, Outputs{


        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val codeValueChanged = PublishSubject.create<String>()
        private val verifyCodePressed = PublishSubject.create<Unit>()

        //Ouputs
        private val validCode = BehaviorSubject.create<Boolean>()
        private val verifyCodeAction = BehaviorSubject.create<Unit>()
        private val loadingEnabled = BehaviorSubject.create<Boolean>()
        private val showError = BehaviorSubject.create<String>()
        private val emailAction = BehaviorSubject.create<String>()


        init {
            intent()
                .filter { it.hasExtra("email") }
                .map { return@map it.getStringExtra("email") }
                .subscribe(this.emailAction)


            this.codeValueChanged
                .map { ValidationService.validateVerificationCode(it) }
                .subscribe(this.validCode)

            val verifyCodeEvent = this.codeValueChanged
                .takeWhen(this.verifyCodePressed)
                .flatMap { this.confirmEmail(it.second) }
                .share()

            verifyCodeEvent
                .filter { it.isFail() }
                .map { "Ocurrió un error al verificar el código. Por favor verificar que el código sea el correcto e intentar de nuevo." }
                .subscribe(this.showError)
            verifyCodeEvent
                .filter { !it.isFail() }
                .map { Unit }
                .subscribe(this.verifyCodeAction)
        }

        override fun codeValueChanged(code: String) {
            return this.codeValueChanged.onNext(code)
        }

        override fun verifyCodePressed() {
            return this.verifyCodePressed.onNext(Unit)
        }

        override fun validCode(): Observable<Boolean> = this.validCode

        override fun verifyCodeAction(): Observable<Unit> = this.verifyCodeAction

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun showError(): Observable<String> = this.showError

        override fun emailAction(): Observable<String> = this.emailAction

        private fun confirmEmail(verificationCode: String) : Observable<Result<Boolean>>{
            return environment.authenticationUseCase().confirmEmail(verificationCode)
                .doOnComplete { this.loadingEnabled.onNext(false) }.doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

    }
}