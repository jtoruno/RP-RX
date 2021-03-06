package com.zimplifica.redipuntos.libs.utils

import android.util.Log
import com.zimplifica.domain.entities.ForgotPasswordError
import com.zimplifica.domain.entities.SignInError
import com.zimplifica.domain.entities.SignUpError
import java.lang.Exception

enum class AuthenticationErrorType{
    SIGN_IN_ERROR
    ,SIGN_UP_ERROR
    ,FORGOT_PASSWORD_ERROR

}

data class ErrorWrapper(var error : Exception, var friendlyMessage: String)

object ErrorHandler {
    fun handleError(error: Exception, authenticationErrorType: AuthenticationErrorType) : ErrorWrapper {
        Log.e("Error handler","",error)
        var errorMessage = "Ocurrió un error"
        when(authenticationErrorType){
            AuthenticationErrorType.SIGN_IN_ERROR ->{
                val signInError = error as? SignInError ?: return ErrorWrapper(error, errorMessage)
                when(signInError){
                    SignInError.invalidCredentials -> errorMessage = "Las credenciales son inválidas"
                    SignInError.userNotConfirmed -> errorMessage = "El usuario no está confirmado"
                    SignInError.tooManyFailedAttempts -> errorMessage = "Has excedido el número de intentos, por favor intenta mas tarde"
                    is SignInError.internalError -> {
                        Log.e("🔴", "SignIn Error: ${signInError.message}")
                        errorMessage = "Ocurrió un error desconocido, por favor contacte a soporte@zimplifica.com"
                    }
                    SignInError.unknown-> errorMessage = "Ocurrió un error desconocido, por favor contacte a soporte@zimplifica.com"
                }
            }
            AuthenticationErrorType.SIGN_UP_ERROR->{
                val signUpError = error as? SignUpError ?: return ErrorWrapper(error, errorMessage)
                when(signUpError){
                    SignUpError.aliasExists->errorMessage = "El usuario ingresado está actualmente registrado en el sistema."
                    SignUpError.codeDeliveryFailure->errorMessage = "Error al enviar el código de verificación."
                    SignUpError.codeMismatch->errorMessage = "Código de verificación inválido. Por favor intentelo de nuevo."
                    SignUpError.expiredCode->errorMessage = "El código de verificación ha expirado. Por favor intentelo de nuevo."
                    is SignUpError.internalError->{
                        Log.e("🔴", "SignIn Error: ${signUpError.message}")
                        errorMessage = "Ocurrió un error desconocido, por favor contacte a soporte@zimplifica.com"
                }
                    SignUpError.passwordResetRequiredException->errorMessage = "Usuario registrado en el sistema. Por favor cambiar contraseña. "
                    SignUpError.tooManyFailedAttemptsException->errorMessage = "Has alcanzado el máximo de intentos fallidos. Por favor intenta más tarde."
                    SignUpError.tooManyRequestsException->errorMessage = "Has alcanzado el máximo de intentos. Por favor intenta más tarde."
                    SignUpError.userNotConfirmedException->errorMessage = "Usuario no confirmado. Por favor proceder a confirmación."
                    SignUpError.usernameExistsException->errorMessage = "El usuario ingresado está actualmente registrado en el sistema."
                    SignUpError.unknown->errorMessage = "Ocurrió un error desconocido, por favor contacte a soporte@zimplifica.com"
                }
            }

            AuthenticationErrorType.FORGOT_PASSWORD_ERROR->{
                val forgotPasswordError = error as? ForgotPasswordError ?: return ErrorWrapper(error, errorMessage)
                when(forgotPasswordError){
                    ForgotPasswordError.userNotFound -> errorMessage="El usuario ingresado no se encuetra registrado. Por favor intentar con un usuario válido."
                    ForgotPasswordError.limitExceeded-> errorMessage = "Límite de intentos excedido. Por favor intente dentro de unos minutos."
                    is ForgotPasswordError.internalError -> {
                        Log.e("🔴", "ForgotPassword Error: ${forgotPasswordError.message}")
                        errorMessage = "Ocurrió un error desconocido, por favor contacte a soporte@zimplifica.com"
                    }
                    ForgotPasswordError.unknown-> errorMessage = "Ocurrió un error desconocido, por favor contacte a soporte@zimplifica.com"
                }
            }
        }
        return ErrorWrapper(error,errorMessage)
    }
}