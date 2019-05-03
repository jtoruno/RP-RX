package com.zimplifica.domain.entities


sealed class SignUpError : Error() {
    object aliasExists : SignUpError()
    object codeDeliveryFailure : SignUpError()
    object codeMismatch: SignUpError()
    object expiredCode: SignUpError()
    data class internalError(override val message: String): SignUpError()
    object passwordResetRequiredException: SignUpError()
    object tooManyFailedAttemptsException: SignUpError()
    object tooManyRequestsException: SignUpError()
    object userNotConfirmedException: SignUpError()
    object usernameExistsException: SignUpError()
    object unknown: SignUpError()
}