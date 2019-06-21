package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface AccountInfoVM {
    interface Inputs {
        fun viewCreated()
        fun verifyEmailPressed()
    }
    interface Outputs {
        /// Emits when the email change button is triggered.
        fun verifyEmailAction() : Observable<Unit>

        /// Emits when the user information action is triggered.
        fun userInformationAction() : Observable<UserInformationResult?>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<AccountInfoVM>(environment),Inputs, Outputs{


        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val verifyEmailPressed = PublishSubject.create<Unit>()
        private val viewCreated = PublishSubject.create<Unit>()

        //Outputs
        private val verifyEmailAction = BehaviorSubject.create<Unit>()
        private val userInformationAction = BehaviorSubject.create<UserInformationResult>()

        init {
            this.verifyEmailPressed
                .subscribe(this.verifyEmailAction)

            viewCreated
                .map { return@map environment.currentUser().getCurrentUser() }
                .subscribe(this.userInformationAction)

            environment.userUseCase().getUserInformationSubscription()
                .subscribe(this.userInformationAction)

        }

        override fun viewCreated() {
            return this.viewCreated.onNext(Unit)
        }

        override fun verifyEmailPressed() {
            return this.verifyEmailPressed.onNext(Unit)
        }

        override fun verifyEmailAction(): Observable<Unit> = this.verifyEmailAction

        override fun userInformationAction(): Observable<UserInformationResult?> = this.userInformationAction

    }
}