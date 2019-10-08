package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.SecurityCode
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ValidationService
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface CreatePinVM {
    interface Inputs {
        // Call when security code textfield changed.
        fun pinChanged(pin: String)

        /// Call when next button is pressed.
        fun nextButtonPressed()
    }
    interface Outputs {
        /// Emits when next button is pressed.
        fun nextButtonAction() : Observable<Boolean>
        /// Emits when next button is enabled.
        fun nextButtonEnabled() : Observable<Boolean>

        /// Emits when loading changes.
        fun loadingEnabled() : Observable<Boolean>

        /// Emits when an error ocurred.
        fun showErrorAction() : Observable<String>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<CreatePinVM>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val pinChanged = BehaviorSubject.create<String>()
        private val nextButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val nextButtonAction = BehaviorSubject.create<Boolean>()
        private val nextButtonEnabled = BehaviorSubject.create<Boolean>()
        private val loadingEnabled = BehaviorSubject.create<Boolean>()
        private val showError = BehaviorSubject.create<String>()

        init {
            pinChanged
                .map { ValidationService.validateSecurityCode(it) }
                .subscribe(nextButtonEnabled)

            val createPinEvent = nextButtonPressed
                .flatMap { return@flatMap this.createPin(pinChanged.value) }
                .share()

            createPinEvent
                .filter { it.isFail() }
                .map { "Ocurrió un error al crear el PIN. Por favor intenta de nuevo." }
                .subscribe(this.showError)

            createPinEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.nextButtonAction)
        }


        override fun pinChanged(pin: String) {
            return this.pinChanged.onNext(pin)
        }

        override fun nextButtonPressed() {
            return this.nextButtonPressed.onNext(Unit)
        }

        override fun nextButtonAction(): Observable<Boolean> = this.nextButtonAction

        override fun nextButtonEnabled(): Observable<Boolean> = this.nextButtonEnabled

        override fun loadingEnabled(): Observable<Boolean> = this.loadingEnabled

        override fun showErrorAction(): Observable<String> = this.showError

        private fun createPin(pin : String) : Observable<Result<Boolean>>{
            val securiteCode = SecurityCode(pin, null)
            return environment.userUseCase().createPin(securiteCode)
                .doOnComplete { this.loadingEnabled.onNext(false) }
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

    }
}