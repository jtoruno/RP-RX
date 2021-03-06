package com.zimplifica.awsplatform.Utils

import java.lang.Exception

sealed class AWSMobileClientError : Exception(){
    data class aliasExists(override val message: String) : AWSMobileClientError()
    data class codeDeliveryFailure(override val message: String) : AWSMobileClientError()
    data class codeMismatch(override val message: String) : AWSMobileClientError()
    data class expiredCode(override val message: String) : AWSMobileClientError()
    data class groupExists(override val message: String) : AWSMobileClientError()
    data class internalError(override val message: String) : AWSMobileClientError()
    data class invalidLambdaResponse(override val message: String) : AWSMobileClientError()
    data class invalidOAuthFlow(override val message: String) : AWSMobileClientError()
    data class invalidParameter(override val message: String) : AWSMobileClientError()
    data class invalidPassword(override val message: String) : AWSMobileClientError()
    data class invalidUserPoolConfiguration(override val message: String) : AWSMobileClientError()
    data class limitExceeded(override val message: String) : AWSMobileClientError()
    data class mfaMethodNotFound(override val message: String) : AWSMobileClientError()
    data class notAuthorized(override val message: String) : AWSMobileClientError()
    data class passwordResetRequired(override val message: String) : AWSMobileClientError()
    data class resourceNotFound(override val message: String) : AWSMobileClientError()
    data class scopeDoesNotExist(override val message: String) : AWSMobileClientError()
    data class softwareTokenMFANotFound(override val message: String) : AWSMobileClientError()
    data class tooManyFailedAttempts(override val message: String) : AWSMobileClientError()
    data class tooManyRequests(override val message: String) : AWSMobileClientError()
    data class unexpectedLambda(override val message: String) : AWSMobileClientError()
    data class userLambdaValidation(override val message: String) : AWSMobileClientError()
    data class userNotConfirmed(override val message: String) : AWSMobileClientError()
    data class userNotFound(override val message: String) : AWSMobileClientError()
    data class usernameExists(override val message: String) : AWSMobileClientError()
    data class unknown(override val message: String) : AWSMobileClientError()
    data class notSignedIn(override val message: String) : AWSMobileClientError()
    data class identityIdUnavailable(override val message: String) : AWSMobileClientError()
    data class guestAccessNotAllowed(override val message: String) : AWSMobileClientError()
    data class federationProviderExists(override val message: String) : AWSMobileClientError()
    data class cognitoIdentityPoolNotConfigured(override val message: String) : AWSMobileClientError()
    data class unableToSignIn(override val message: String) : AWSMobileClientError()
    data class invalidState(override val message: String) : AWSMobileClientError()
    data class userPoolNotConfigured(override val message: String) : AWSMobileClientError()
    data class userCancelledSignIn(override val message: String) : AWSMobileClientError()
    data class invalidConfiguration(override val message: String): AWSMobileClientError()

    data class deviceNotRemembered(override val message: String): AWSMobileClientError()
    data class idTokenAndAcceessTokenNotIssued(override val message: String): AWSMobileClientError()
    data class idTokenNotIssued(override val message: String): AWSMobileClientError()
    data class securityFailed(override val message: String): AWSMobileClientError()
    data class errorLoadingPage(override val message: String): AWSMobileClientError()
    data class expiredRefreshToken(override val message: String): AWSMobileClientError()
    data class badRequest(override val message: String): AWSMobileClientError()
}