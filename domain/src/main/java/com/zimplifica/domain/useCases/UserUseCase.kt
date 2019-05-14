package com.zimplifica.domain.useCases

import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import io.reactivex.Observable

interface UserUseCase{
    fun getUserInformation(useCache : Boolean) :Observable<Result<UserInformationResult>>
}