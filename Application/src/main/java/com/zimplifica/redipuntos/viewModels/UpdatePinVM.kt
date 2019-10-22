package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.SecurityCode
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ValidationService
import com.zimplifica.redipuntos.ui.data.contactEmail
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface UpdatePinVM {
    interface Inputs {
        /// Call when the view did load.
        fun viewDidLoad()

        /// Call when verification code textfield changed.
        fun verificationCodeChanged(verificationCode: String)

        /// Call when security code textfield changed.
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

        /// Emits when view did load to return user information.
        fun userInformationAction() : Observable<UserInformationResult>

        /// Emits when an error ocurred.
        fun showErrorAction() : Observable<String>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<UpdatePinVM>(environment),Inputs, Outputs {

        val inputs : Inputs = this
        val outputs : Outputs = this
        private val resources = RPApplication.applicationContext().resources


        //Inputs
        private val viewDidLoad = PublishSubject.create<Unit>()
        private val verificationCodeChanged = PublishSubject.create<String>()
        private val pinChanged = PublishSubject.create<String>()
        private val nextButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val nextButtonAction = BehaviorSubject.create<Boolean>()
        private val nextButtonEnabled = BehaviorSubject.create<Boolean>()
        private val loadingEnabled = BehaviorSubject.create<Boolean>()
        private var userInformationAction : Observable<UserInformationResult>
        private val showErrorAction =  BehaviorSubject.create<String>()

        init {
            userInformationAction = Observable.fromArray(environment.currentUser().getCurrentUser())



            val verifyPhoneEvent = viewDidLoad
                .flatMap { return@flatMap this.verifyPhoneNumber() }
                .share()

            verifyPhoneEvent
                .filter { !it.isFail() }
                .subscribe { Unit }

            val formData = Observables.combineLatest(pinChanged,verificationCodeChanged)

            formData
                .map { ValidationService.validateVerificationAndSecurityCode(it.first,it.second) }
                .subscribe(this.nextButtonEnabled)

            val updatePinEvent = formData
                .takeWhen(this.nextButtonPressed)
                .flatMap {
                    val pinCode = it.second.first
                    val verificationCode = it.second.second
                    return@flatMap  this.updatePin(pinCode,verificationCode)
                }
                .share()

            updatePinEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.nextButtonAction)

            Observable.merge(verifyPhoneEvent,updatePinEvent)
                .filter { it.isFail() }
                .map { return@map resources.getString(R.string.Error_unkown_error, contactEmail) }
                .subscribe(this.showErrorAction)

        }

        override fun viewDidLoad() {
            return this.viewDidLoad.onNext(Unit)
        }

        override fun verificationCodeChanged(verificationCode: String) {
            return this.verificationCodeChanged.onNext(verificationCode)
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

        override fun userInformationAction(): Observable<UserInformationResult> = this.userInformationAction

        override fun showErrorAction(): Observable<String> = this.showErrorAction

        private fun updatePin(newPing: String, verificationCode: String) : Observable<Result<Boolean>>{
            val securityCode = SecurityCode(newPing,verificationCode)
            return environment.userUseCase().updatePin(securityCode)
                .doOnComplete { this.loadingEnabled.onNext(false) }
                .doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

        private fun verifyPhoneNumber() : Observable<Result<Boolean>> {
            return environment.userUseCase().verifyPhoneNumber()
                //.doOnComplete { this.loadingEnabled.onNext(false) }
                //.doOnSubscribe { this.loadingEnabled.onNext(true) }
        }

    }
}