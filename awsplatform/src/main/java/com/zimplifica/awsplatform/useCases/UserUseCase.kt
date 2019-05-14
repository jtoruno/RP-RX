package com.zimplifica.awsplatform.useCases

import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.useCases.UserUseCase
import io.reactivex.Observable
import io.reactivex.Single

class UserUseCase : UserUseCase {
    override fun getUserInformation(useCache: Boolean): Observable<Result<UserInformationResult>> {
        val single = Single.create<Result<UserInformationResult>> create@{ single->

        }
        return single.toObservable()
    }
}