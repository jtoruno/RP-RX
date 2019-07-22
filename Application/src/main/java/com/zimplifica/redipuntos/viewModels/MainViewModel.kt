package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface MainViewModel {
    interface Inputs {
        fun signInButtonClicked()
        fun signUpButtonClicked()
        fun helpButtonClicked()
    }
    interface Outputs {
        fun startSignInActivity() : Observable<Unit>
        fun startSignUpActivity() : Observable<Unit>
        fun startHelpActivity() : Observable<Unit>
    }

    class ViewModel(@NonNull val environment: Environment):ActivityViewModel<MainViewModel>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val signInButtonClicked = PublishSubject.create<Unit>()
        private val signUpButtonClicked = PublishSubject.create<Unit>()
        private val helpButtonClicked = PublishSubject.create<Unit>()

        //Outputs
        private val startSignInActivity : Observable<Unit>
        private val startSignUpActivity : Observable<Unit>
        private val startHelpActivity : Observable<Unit>

        init {
            this.startSignInActivity = this.signInButtonClicked
            this.startSignUpActivity = this.signUpButtonClicked
            this.startHelpActivity = this.helpButtonClicked
        }

        override fun signInButtonClicked() = this.signInButtonClicked.onNext(Unit)

        override fun signUpButtonClicked() = this.signUpButtonClicked.onNext(Unit)

        override fun helpButtonClicked() = this.helpButtonClicked.onNext(Unit)

        override fun startSignInActivity(): Observable<Unit> = this.startSignInActivity

        override fun startSignUpActivity(): Observable<Unit> = this.startSignUpActivity

        override fun startHelpActivity(): Observable<Unit> = this.startHelpActivity

    }
}