package com.zimplifica.domain.useCases

import com.zimplifica.domain.entities.*
import io.reactivex.Observable

interface UserUseCase{
    fun getUserInformation(useCache : Boolean) :Observable<Result<UserInformationResult>>
    fun updateUserInfo(citizen: CitizenInput) : Observable<Result<Citizen>>
    fun getVendorInformation(vendorId: String) : Observable<Result<Vendor>>
}