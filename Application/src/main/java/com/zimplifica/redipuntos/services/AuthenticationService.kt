package com.zimplifica.redipuntos.services

import com.zimplifica.domain.entities.*
import com.zimplifica.domain.useCases.AuthenticationUseCase
import io.reactivex.Observable

class AuthenticationService(private val state : GlobalState, private val authenticationUseCase : AuthenticationUseCase) {

    fun signIn(username: String, password: String) : Observable<Result<SignInResult>> {
        return authenticationUseCase.signIn(username, password)
    }

    fun getCurrentUserState() : Observable<UserStateResult> {
        return authenticationUseCase.getCurrentUserState()
    }

    fun signUp(userId: String, username: String, password: String,verificationCode: String) : Observable<Result<SignUpResult>> {
        return authenticationUseCase.signUp(userId, username, password,verificationCode)
    }

    fun verifyPhoneNumber(phoneNumber: String) : Observable<Result<Boolean>> {
        return authenticationUseCase.verifyPhoneNumber(phoneNumber)
    }

    fun resendVerificationCode(userId: String) : Observable<Result<SignUpResendConfirmationResult>> {
        return authenticationUseCase.resendVerificationCode(userId)
    }

    fun signOut() : Observable<Result<UserStateResult>> {
        return authenticationUseCase.signOut()
    }

    fun forgotPassword(username: String) : Observable<Result<ForgotPasswordResult>> {
        return authenticationUseCase.forgotPassword(username)
    }

    fun confirmForgotPassword(username: String, confirmationCode: String, newPassword: String) : Observable<Result<ForgotPasswordResult>> {
        return authenticationUseCase.confirmForgotPassword(username, confirmationCode, newPassword)
    }

    fun updateUserAttributes(attributes: Map<String, String>) : Observable<Result<Boolean>> {
        return authenticationUseCase.updateUserAttributes(attributes)
            .doOnNext { result ->
                when(result){
                    is Result.success ->{
                        val email = attributes["email"]
                        this.state.updateCurrentUser(email?:"")
                    }
                    else -> return@doOnNext
                }
            }
    }

    fun verifyEmail() : Observable<Result<String>> {
        return authenticationUseCase.verifyEmail()
    }

    fun confirmEmail(verificationCode: String) : Observable<Result<Boolean>> {
        return authenticationUseCase.confirmEmail(verificationCode)
            .doOnNext { result ->
                when(result){
                    is Result.success ->{
                        this.state.updateCurrentUser(true)
                    }
                    else -> return@doOnNext
                }
            }
    }
}