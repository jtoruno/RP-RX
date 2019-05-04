package com.zimplifica.domain.entities


enum class SignUpConfirmationState {
    confirmed, unconfirmed, unknown
}
class SignUpConfirmationResult(state: SignUpConfirmationState) {
    var state : SignUpConfirmationState = state
}