package com.zimplifica.domain.useCases

import com.zimplifica.domain.entities.*
import io.reactivex.Observable

interface AuthenticationUseCase {
    fun signIn(username: String,password: String) : Observable<Result<SignInResult>>
    fun signUp(userId: String, username: String, password: String) : Observable<Result<SignUpResult>>
    fun confirmSignUp(userId: String,verificationCode: String):Observable<Result<SignUpConfirmationResult>>
    fun resendVerificationCode(userId: String) : Observable<Result<SignUpResendConfirmationResult>>
    fun forgotPassword(username : String): Observable<Result<ForgotPasswordResult>>
    fun confirmForgotPassword(username : String, confirmationCode: String, newPassword : String) : Observable<Result<ForgotPasswordResult>>
}