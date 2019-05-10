package com.zimplifica.awsplatform.Utils

import android.util.Log
import com.amazonaws.AmazonServiceException
import com.zimplifica.domain.entities.ForgotPasswordError
import com.zimplifica.domain.entities.SignInError
import com.zimplifica.domain.entities.SignUpError
import java.lang.Exception

class AWSErrorDecoder{
    companion object {

        fun decodeSignInError(error: Exception?) : SignInError{
            Log.e("Error decoder", "SignIn",error)
            var signInError : SignInError = SignInError.unknown
            (error as? AWSMobileClientError)?.let {
                when(it){
                    AWSMobileClientError.invalidPassword(it.message!!) -> signInError= SignInError.invalidCredentials
                    AWSMobileClientError.userNotFound(it.message!!) -> signInError=SignInError.invalidCredentials
                    AWSMobileClientError.userNotConfirmed(it.message!!) -> signInError=SignInError.userNotConfirmed
                    AWSMobileClientError.tooManyFailedAttempts(it.message!!) -> signInError=SignInError.tooManyFailedAttempts

                    AWSMobileClientError.aliasExists(it.message!!),
                    AWSMobileClientError.codeDeliveryFailure(it.message!!),
                    AWSMobileClientError.codeMismatch(it.message!!),
                    AWSMobileClientError.expiredCode(it.message!!),
                    AWSMobileClientError.groupExists(it.message!!),
                    AWSMobileClientError.internalError(it.message!!),
                    AWSMobileClientError.invalidLambdaResponse(it.message!!),
                    AWSMobileClientError.invalidOAuthFlow(it.message!!),
                    AWSMobileClientError.invalidParameter(it.message!!),
                    AWSMobileClientError.invalidUserPoolConfiguration(it.message!!),
                    AWSMobileClientError.limitExceeded(it.message!!),
                    AWSMobileClientError.mfaMethodNotFound(it.message!!),
                    AWSMobileClientError.notAuthorized(it.message!!),
                    AWSMobileClientError.passwordResetRequired(it.message!!),
                    AWSMobileClientError.resourceNotFound(it.message!!),
                    AWSMobileClientError.scopeDoesNotExist(it.message!!),
                    AWSMobileClientError.softwareTokenMFANotFound(it.message!!),
                    AWSMobileClientError.tooManyRequests(it.message!!),
                    AWSMobileClientError.unexpectedLambda(it.message!!),
                    AWSMobileClientError.userLambdaValidation(it.message!!),
                    AWSMobileClientError.usernameExists(it.message!!),
                    AWSMobileClientError.unknown(it.message!!),
                    AWSMobileClientError.notSignedIn(it.message!!),
                    AWSMobileClientError.identityIdUnavailable(it.message!!),
                    AWSMobileClientError.guestAccessNotAllowed(it.message!!),
                    AWSMobileClientError.federationProviderExists(it.message!!),
                    AWSMobileClientError.cognitoIdentityPoolNotConfigured(it.message!!),
                    AWSMobileClientError.unableToSignIn(it.message!!),
                    AWSMobileClientError.invalidState(it.message!!),
                    AWSMobileClientError.userPoolNotConfigured(it.message!!),
                    AWSMobileClientError.userCancelledSignIn(it.message!!),
                    AWSMobileClientError.badRequest(it.message!!),
                    AWSMobileClientError.expiredRefreshToken(it.message!!),
                    AWSMobileClientError.errorLoadingPage(it.message!!),
                    AWSMobileClientError.securityFailed(it.message!!),
                    AWSMobileClientError.idTokenNotIssued(it.message!!),
                    AWSMobileClientError.idTokenAndAcceessTokenNotIssued(it.message!!),
                    AWSMobileClientError.invalidConfiguration(it.message!!),
                    AWSMobileClientError.deviceNotRemembered(it.message!!) -> signInError = SignInError.internalError(it.message!!)

                }
            } ?: run {
                signInError = SignInError.unknown
            }
            return signInError
        }

        fun decodeSignUpError(error : Exception?) : SignUpError{
            var signUpError : SignUpError = SignUpError.unknown
            (error as? AWSMobileClientError)?.let {
                when(it){
                    AWSMobileClientError.aliasExists(it.message!!) -> signUpError = SignUpError.aliasExists
                    AWSMobileClientError.codeDeliveryFailure(it.message!!) -> signUpError =SignUpError.codeDeliveryFailure
                    AWSMobileClientError.codeMismatch(it.message!!) -> signUpError =SignUpError.codeMismatch
                    AWSMobileClientError.expiredCode(it.message!!)  -> signUpError =SignUpError.expiredCode
                    AWSMobileClientError.passwordResetRequired(it.message!!)  -> signUpError =SignUpError.passwordResetRequiredException
                    AWSMobileClientError.tooManyFailedAttempts(it.message!!)  -> signUpError =SignUpError.tooManyFailedAttemptsException
                    AWSMobileClientError.tooManyRequests(it.message!!)  -> signUpError =SignUpError.tooManyRequestsException
                    AWSMobileClientError.userNotConfirmed(it.message!!)  -> signUpError =SignUpError.userNotConfirmedException
                    AWSMobileClientError.usernameExists(it.message!!) -> signUpError =SignUpError.usernameExistsException

                    AWSMobileClientError.invalidConfiguration(it.message!!),
                    AWSMobileClientError.deviceNotRemembered(it.message!!) ,
                    AWSMobileClientError.idTokenAndAcceessTokenNotIssued(it.message!!) ,
                    AWSMobileClientError.idTokenNotIssued(it.message!!) ,
                    AWSMobileClientError.securityFailed(it.message!!) ,
                    AWSMobileClientError.errorLoadingPage(it.message!!) ,
                    AWSMobileClientError.expiredRefreshToken(it.message!!) ,
                    AWSMobileClientError.badRequest(it.message!!) ,
                    AWSMobileClientError.unknown(it.message!!) ,
                    AWSMobileClientError.notSignedIn(it.message!!) ,
                    AWSMobileClientError.identityIdUnavailable(it.message!!) ,
                    AWSMobileClientError.guestAccessNotAllowed(it.message!!) ,
                    AWSMobileClientError.federationProviderExists(it.message!!) ,
                    AWSMobileClientError.cognitoIdentityPoolNotConfigured(it.message!!) ,
                    AWSMobileClientError.unableToSignIn(it.message!!) ,
                    AWSMobileClientError.invalidState(it.message!!) ,
                    AWSMobileClientError.userPoolNotConfigured(it.message!!),
                    AWSMobileClientError.userCancelledSignIn(it.message!!) ,
                    AWSMobileClientError.userNotFound(it.message!!) ,
                    AWSMobileClientError.unexpectedLambda(it.message!!) ,
                    AWSMobileClientError.userLambdaValidation(it.message!!) ,
                    AWSMobileClientError.resourceNotFound(it.message!!) ,
                    AWSMobileClientError.scopeDoesNotExist(it.message!!) ,
                    AWSMobileClientError.softwareTokenMFANotFound(it.message!!) ,
                    AWSMobileClientError.groupExists(it.message!!) ,
                    AWSMobileClientError.internalError(it.message!!) ,
                    AWSMobileClientError.invalidLambdaResponse(it.message!!) ,
                    AWSMobileClientError.invalidOAuthFlow(it.message!!) ,
                    AWSMobileClientError.invalidParameter(it.message!!) ,
                    AWSMobileClientError.invalidPassword(it.message!!) ,
                    AWSMobileClientError.invalidUserPoolConfiguration(it.message!!) ,
                    AWSMobileClientError.limitExceeded(it.message!!) ,
                    AWSMobileClientError.mfaMethodNotFound(it.message!!) ,
                    AWSMobileClientError.notAuthorized(it.message!!) -> signUpError = SignUpError.internalError(it.message!!)
                }
            } ?: run {
                signUpError = SignUpError.unknown
            }
            return signUpError
        }

        fun decodeForgotPasswordError(error : Exception?) : ForgotPasswordError{
            var forgotPasswordError: ForgotPasswordError = ForgotPasswordError.unknown
            (error as? AWSMobileClientError)?.let {
                when(it){
                    AWSMobileClientError.userNotFound(it.message!!) -> forgotPasswordError = ForgotPasswordError.userNotFound
                    AWSMobileClientError.limitExceeded(it.message!!) -> forgotPasswordError = ForgotPasswordError.limitExceeded

                    AWSMobileClientError.invalidConfiguration(it.message!!),
                    AWSMobileClientError.deviceNotRemembered(it.message!!) ,
                    AWSMobileClientError.idTokenAndAcceessTokenNotIssued(it.message!!) ,
                    AWSMobileClientError.idTokenNotIssued(it.message!!) ,
                    AWSMobileClientError.securityFailed(it.message!!) ,
                    AWSMobileClientError.errorLoadingPage(it.message!!) ,
                    AWSMobileClientError.expiredRefreshToken(it.message!!) ,
                    AWSMobileClientError.badRequest(it.message!!) ,
                    AWSMobileClientError.unknown(it.message!!) ,
                    AWSMobileClientError.notSignedIn(it.message!!) ,
                    AWSMobileClientError.identityIdUnavailable(it.message!!) ,
                    AWSMobileClientError.guestAccessNotAllowed(it.message!!) ,
                    AWSMobileClientError.federationProviderExists(it.message!!) ,
                    AWSMobileClientError.cognitoIdentityPoolNotConfigured(it.message!!) ,
                    AWSMobileClientError.unableToSignIn(it.message!!) ,
                    AWSMobileClientError.invalidState(it.message!!) ,
                    AWSMobileClientError.userPoolNotConfigured(it.message!!),
                    AWSMobileClientError.userCancelledSignIn(it.message!!) ,
                    AWSMobileClientError.unexpectedLambda(it.message!!) ,
                    AWSMobileClientError.userLambdaValidation(it.message!!) ,
                    AWSMobileClientError.resourceNotFound(it.message!!) ,
                    AWSMobileClientError.scopeDoesNotExist(it.message!!) ,
                    AWSMobileClientError.softwareTokenMFANotFound(it.message!!) ,
                    AWSMobileClientError.groupExists(it.message!!) ,
                    AWSMobileClientError.internalError(it.message!!) ,
                    AWSMobileClientError.invalidLambdaResponse(it.message!!) ,
                    AWSMobileClientError.invalidOAuthFlow(it.message!!) ,
                    AWSMobileClientError.invalidParameter(it.message!!) ,
                    AWSMobileClientError.invalidPassword(it.message!!) ,
                    AWSMobileClientError.invalidUserPoolConfiguration(it.message!!) ,
                    AWSMobileClientError.aliasExists(it.message!!) ,
                    AWSMobileClientError.mfaMethodNotFound(it.message!!) ,

                    AWSMobileClientError.codeDeliveryFailure(it.message!!) ,
                    AWSMobileClientError.codeMismatch(it.message!!),
                    AWSMobileClientError.expiredCode(it.message!!) ,
                    AWSMobileClientError.passwordResetRequired(it.message!!) ,
                    AWSMobileClientError.tooManyFailedAttempts(it.message!!),
                    AWSMobileClientError.tooManyRequests(it.message!!)  ,
                    AWSMobileClientError.userNotConfirmed(it.message!!) ,
                    AWSMobileClientError.usernameExists(it.message!!),

                    AWSMobileClientError.notAuthorized(it.message!!) -> forgotPasswordError = ForgotPasswordError.internalError(it.message!!)


                }

            } ?: run {
                forgotPasswordError = ForgotPasswordError.unknown
            }
            return forgotPasswordError
        }
    }
}