package com.zimplifica.awsplatform.useCases

import android.content.Context
import com.zimplifica.awsplatform.AppSync.AppSyncClient
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.useCases.UserUseCase
import io.reactivex.Observable
import io.reactivex.Single

class UserUseCase : UserUseCase {

    //val client = AppSyncClient(context).client

    override fun getUserInformation(useCache: Boolean): Observable<Result<UserInformationResult>> {
        val single = Single.create<Result<UserInformationResult>> create@{ single->

        }
        return single.toObservable()
    }
}