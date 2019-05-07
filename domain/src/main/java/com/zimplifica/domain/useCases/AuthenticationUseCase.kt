package com.zimplifica.domain.useCases

import com.zimplifica.domain.entities.SignInResult
import com.zimplifica.domain.entities.SignUpConfirmationResult
import com.zimplifica.domain.entities.SignUpResendConfirmationResult
import com.zimplifica.domain.entities.SignUpResult
import io.reactivex.Observable
import com.zimplifica.domain.entities.Result

interface AuthenticationUseCase {
    fun signIn(username: String,password: String) : Observable<Result<SignInResult>>
    fun signUp(userId: String, username: String, password: String) : Observable<Result<SignUpResult>>
    fun confirmSignUp(userId: String,verificationCode: String):Observable<Result<SignUpConfirmationResult>>
    fun resendVerificationCode(userId: String) : Observable<Result<SignUpResendConfirmationResult>>
}