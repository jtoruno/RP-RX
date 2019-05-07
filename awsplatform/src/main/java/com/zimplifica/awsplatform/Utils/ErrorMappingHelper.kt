package com.zimplifica.awsplatform.Utils

import com.amazonaws.AmazonServiceException
import com.amazonaws.mobile.client.AWSMobileClient
import java.lang.Exception

fun ErrorMappingHelper(errorCode : String,message : String, error: Exception?): AWSMobileClientError{
    when(errorCode){
        "AliasExistsException" -> return AWSMobileClientError.aliasExists(message)
        "CodeDeliveryFailureException" -> return AWSMobileClientError.codeDeliveryFailure(message)
        "CodeMismatchException" -> return AWSMobileClientError.codeMismatch(message)
        "ExpiredCodeException" -> return AWSMobileClientError.expiredCode(message)
        "GroupExistsException" -> return AWSMobileClientError.groupExists(message)
        "InternalErrorException" -> return AWSMobileClientError.internalError(message)
        "InvalidLambdaResponseException" -> return AWSMobileClientError.invalidLambdaResponse(message)
        "InvalidOAuthFlowException" -> return AWSMobileClientError.invalidOAuthFlow(message)
        "InvalidParameterException" -> return AWSMobileClientError.invalidParameter(message)
        "InvalidPasswordException" -> return AWSMobileClientError.invalidPassword(message)
        "InvalidUserPoolConfigurationException" -> AWSMobileClientError.invalidUserPoolConfiguration(message)
        "LimitExceededException" -> return AWSMobileClientError.limitExceeded(message)
        "MFAMethodNotFoundException" -> return AWSMobileClientError.mfaMethodNotFound(message)
        "NotAuthorizedException" -> return AWSMobileClientError.notAuthorized(message)
        "PasswordResetRequiredException" -> return AWSMobileClientError.passwordResetRequired(message)
        "ResourceNotFoundException" -> return AWSMobileClientError.resourceNotFound(message)
        "ScopeDoesNotExistException" -> return AWSMobileClientError.scopeDoesNotExist(message)
        "SoftwareTokenMFANotFoundException" -> return AWSMobileClientError.softwareTokenMFANotFound(message)
        "TooManyFailedAttemptsException" -> return AWSMobileClientError.tooManyFailedAttempts(message)
        "TooManyRequestsException" -> return AWSMobileClientError.tooManyRequests(message)
        "UnexpectedLambdaException" -> return AWSMobileClientError.unexpectedLambda(message)
        "UserLambdaValidationException" -> return AWSMobileClientError.userLambdaValidation(message)
        "UserNotConfirmedException" -> return AWSMobileClientError.userNotConfirmed(message)
        "UserNotFoundException" -> return AWSMobileClientError.userNotFound(message)
        "UsernameExistsException" -> return AWSMobileClientError.usernameExists(message)
    }

    return if (error != null && error is AmazonServiceException) {
        AWSMobileClientError.unknown("${(error.errorType)}: $message")
    }else{
        AWSMobileClientError.unknown(message)
    }
}