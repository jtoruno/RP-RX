package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface PremiumVM {
    interface Inputs {
        /// Call when enable premium button is pressed.
        fun enablePremiumButtonPressed()

        /// Call when terms and conditions button is pressed.
        fun termsAndConditionsButtonPressed()
    }

    interface Outputs {
        /// Emits when enable premium button is pressed.
        fun enablePremiumAction() : Observable<Unit>

        /// Emits when terms and conditions button is pressed.
        fun termsAndConditionsAction() : Observable<Unit>
    }

    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<PremiumVM>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val enablePremiumButtonPressed = PublishSubject.create<Unit>()
        private val termsAndConditionsButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val enablePremiumAction = BehaviorSubject.create<Unit>()
        private val termsAndConditionsAction = BehaviorSubject.create<Unit>()

        init {
            this.enablePremiumButtonPressed
                .subscribe(this.enablePremiumAction)

            this.termsAndConditionsButtonPressed
                .subscribe(this.termsAndConditionsAction)
        }

        override fun enablePremiumButtonPressed() {
            return this.enablePremiumButtonPressed.onNext(Unit)
        }

        override fun termsAndConditionsButtonPressed() {
            return this.termsAndConditionsButtonPressed.onNext(Unit)
        }

        override fun enablePremiumAction(): Observable<Unit> = this.enablePremiumAction

        override fun termsAndConditionsAction(): Observable<Unit> = this.termsAndConditionsAction

    }
}