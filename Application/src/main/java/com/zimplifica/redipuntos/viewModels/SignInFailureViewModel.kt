package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface SignInFailureViewModel {

    interface Inputs{
        fun forgotPasswordButtonPressed()
        fun signUpButtonPressed()
    }

    interface Outputs{
        fun forgotPasswordButton() : Observable<Unit>
        fun signUpButton() : Observable<Unit>
    }

    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<SignUpVerifyViewModel>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this
        //Inputs
        private val forgotPasswordButtonPressed = PublishSubject.create<Unit>()
        private val signUpButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val forgotPasswordButton : Observable<Unit>
        private val signUpButton : Observable<Unit>

        init {
            this.forgotPasswordButton = this.forgotPasswordButtonPressed
            this.signUpButton = this.signUpButtonPressed
        }

        override fun forgotPasswordButtonPressed() = this.forgotPasswordButtonPressed.onNext(Unit)

        override fun signUpButtonPressed() = this.signUpButtonPressed.onNext(Unit)

        override fun forgotPasswordButton(): Observable<Unit> = this.forgotPasswordButton

        override fun signUpButton(): Observable<Unit> = this.signUpButton

    }
}