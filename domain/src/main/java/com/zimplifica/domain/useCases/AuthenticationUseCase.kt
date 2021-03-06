package com.zimplifica.domain.useCases

import com.zimplifica.domain.entities.*
import io.reactivex.Observable

interface AuthenticationUseCase {
    fun signIn(username: String,password: String) : Observable<Result<SignInResult>>
    fun getCurrentUserState() : Observable<UserStateResult>
    fun signUp(userId: String, username: String, password: String, verificationCode: String) : Observable<Result<SignUpResult>>
    fun resendVerificationCode(userId: String) : Observable<Result<SignUpResendConfirmationResult>>
    fun signOut() : Observable<Result<UserStateResult>>
    fun forgotPassword(username : String): Observable<Result<ForgotPasswordResult>>
    fun confirmForgotPassword(username : String, confirmationCode: String, newPassword : String) : Observable<Result<ForgotPasswordResult>>
    fun updateUserAttributes( attributes: Map<String, String>) : Observable<Result<Boolean>>
    fun verifyEmail() : Observable<Result<String>>
    fun confirmEmail(verificationCode: String) : Observable<Result<Boolean>>

    fun verifyPhoneNumber(phoneNumber: String) : Observable<Result<Boolean>>
    //fun confirmSignUp(userId: String,verificationCode: String):Observable<Result<SignUpConfirmationResult>>

    fun userStateSubscription() : Observable<UserStateResult>

}