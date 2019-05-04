package com.zimplifica.domain.entities

enum class SignUpResendConfirmationState {
    confirmed, unconfirmed, unknown
}
class SignUpResendConfirmationResult(state: SignUpResendConfirmationState) {
    var state : SignUpResendConfirmationState = state
}