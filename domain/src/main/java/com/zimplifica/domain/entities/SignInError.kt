package com.zimplifica.domain.entities

import java.lang.Exception

sealed class SignInError : Exception(){
    object invalidCredentials : SignInError()
    object userNotConfirmed : SignInError()
    object tooManyFailedAttempts : SignInError()
    data class internalError(override  val message: String) : SignInError()
    object unknown : SignInError()
}