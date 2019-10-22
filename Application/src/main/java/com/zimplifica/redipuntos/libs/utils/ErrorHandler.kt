package com.zimplifica.redipuntos.libs.utils

import android.util.Log
import com.zimplifica.domain.entities.ForgotPasswordError
import com.zimplifica.domain.entities.SignInError
import com.zimplifica.domain.entities.SignUpError
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.ui.data.contactEmail
import java.lang.Exception

enum class AuthenticationErrorType{
    SIGN_IN_ERROR
    ,SIGN_UP_ERROR
    ,FORGOT_PASSWORD_ERROR

}

data class ErrorWrapper(var error : Exception, var friendlyMessage: String)

object ErrorHandler {
    private val resources = RPApplication.applicationContext()
    fun handleError(error: Exception, authenticationErrorType: AuthenticationErrorType) : ErrorWrapper {
        Log.e("Error handler","",error)
        var errorMessage = resources.getString(R.string.Error_unkown_error, contactEmail)
        when(authenticationErrorType){
            AuthenticationErrorType.SIGN_IN_ERROR ->{
                val signInError = error as? SignInError ?: return ErrorWrapper(error, errorMessage)
                when(signInError){
                    SignInError.invalidCredentials -> errorMessage = resources.getString(R.string.Error_invalid_credentials)
                    SignInError.userNotConfirmed -> errorMessage = resources.getString(R.string.Error_user_not_confirmed)
                    SignInError.tooManyFailedAttempts -> errorMessage = resources.getString(R.string.Error_max_attempts_reached)
                    is SignInError.internalError -> {
                        Log.e("🔴", "SignIn Error: ${signInError.message}")
                        errorMessage = resources.getString(R.string.Error_unkown_error, contactEmail)
                    }
                    SignInError.unknown-> errorMessage = resources.getString(R.string.Error_unkown_error, contactEmail)
                }
            }
            AuthenticationErrorType.SIGN_UP_ERROR->{
                val signUpError = error as? SignUpError ?: return ErrorWrapper(error, errorMessage)
                when(signUpError){
                    SignUpError.aliasExists->errorMessage = resources.getString(R.string.Error_user_currently_enrolled,resources.getString(R.string.app_name))
                    SignUpError.codeDeliveryFailure->errorMessage = resources.getString(R.string.Error_verification_code_not_sent)
                    SignUpError.codeMismatch->errorMessage = resources.getString(R.string.Error_verification_code_invalid)
                    SignUpError.expiredCode->errorMessage = resources.getString(R.string.Error_verification_code_expired)
                    is SignUpError.internalError->{
                        Log.e("🔴", "SignIn Error: ${signUpError.message}")
                        errorMessage = resources.getString(R.string.Error_unkown_error, contactEmail)
                }
                    SignUpError.passwordResetRequiredException->errorMessage = resources.getString(R.string.Error_user_enrolled_change_password,resources.getString(R.string.app_name))
                    SignUpError.tooManyFailedAttemptsException->errorMessage = resources.getString(R.string.Error_max_attempts_reached)
                    SignUpError.tooManyRequestsException->errorMessage = resources.getString(R.string.Error_max_attempts_reached)
                    SignUpError.userNotConfirmedException->errorMessage = resources.getString(R.string.Error_user_not_confirmed)
                    SignUpError.usernameExistsException->errorMessage = resources.getString(R.string.Error_user_currently_enrolled,resources.getString(R.string.app_name))
                    SignUpError.unknown->errorMessage = resources.getString(R.string.Error_unkown_error, contactEmail)
                }
            }

            AuthenticationErrorType.FORGOT_PASSWORD_ERROR->{
                val forgotPasswordError = error as? ForgotPasswordError ?: return ErrorWrapper(error, errorMessage)
                when(forgotPasswordError){
                    ForgotPasswordError.userNotFound -> errorMessage= resources.getString(R.string.Error_user_not_enrolled)
                    ForgotPasswordError.limitExceeded-> errorMessage = resources.getString(R.string.Error_max_attempts_reached)
                    is ForgotPasswordError.internalError -> {
                        Log.e("🔴", "ForgotPassword Error: ${forgotPasswordError.message}")
                        errorMessage = resources.getString(R.string.Error_unkown_error, contactEmail)
                    }
                    ForgotPasswordError.unknown-> errorMessage = resources.getString(R.string.Error_unkown_error, contactEmail)
                }
            }
        }
        return ErrorWrapper(error,errorMessage)
    }
}