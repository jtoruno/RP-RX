package com.zimplifica.domain.entities


enum class SignUpState {
    confirmed, unconfirmed, unknown
}
class SignUpResult(state:SignUpState, username : String, password : String) {
    var state : SignUpState = state
    var username : String = username
    var password : String = password
}
