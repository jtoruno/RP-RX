package com.zimplifica.domain.entities

import java.lang.Exception

sealed class ForgotPasswordError : Exception(){
    object userNotFound : ForgotPasswordError()
    object limitExceeded : ForgotPasswordError()
    data class internalError(override val message: String): ForgotPasswordError()
    object unknown : ForgotPasswordError()
}