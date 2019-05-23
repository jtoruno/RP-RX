package com.zimplifica.redipuntos.services

import com.zimplifica.domain.entities.Citizen
import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject

class GlobalState{
    private val userInformationSubscription : ReplaySubject<UserInformationResult> = ReplaySubject.create(1)

    init {
        this.userInformationSubscription.subscribe { userInfo ->

        }
    }

    fun getUserInformationSubscription(): Observable<UserInformationResult> {
        return this.userInformationSubscription
    }

    fun updateCurrentUser(citizen: Citizen) {

    }

    fun updateCurrentUser(paymentMethod: PaymentMethod){

    }

    fun updateCurrentUser(user: UserInformationResult){
        this.userInformationSubscription.onNext(user)
    }

    fun updateCurrentUser(email: String) {

    }

    fun updateCurrentUser( isVerify: Boolean) {

    }

}