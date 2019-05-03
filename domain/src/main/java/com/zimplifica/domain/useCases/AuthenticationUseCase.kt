package com.zimplifica.domain.useCases

import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.SignUpError
//import com.zimplifica.domain.Entities.Result
import com.zimplifica.domain.entities.SignUpResult
import io.reactivex.Observable

interface AuthenticationUseCase {
    fun signUp(userId: String, username: String, password: String) : Observable<Result<SignUpResult, SignUpError>>
}