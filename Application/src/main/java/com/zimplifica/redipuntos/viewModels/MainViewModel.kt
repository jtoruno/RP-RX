package com.zimplifica.redipuntos.viewModels

import io.reactivex.Observable

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
}