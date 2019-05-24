package com.zimplifica.redipuntos.services

import android.annotation.SuppressLint
import android.content.Context
import com.zimplifica.domain.entities.Citizen
import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject

@SuppressLint("CheckResult")
class GlobalState(val context: Context){
    private val userInformationSubscription : ReplaySubject<UserInformationResult> = ReplaySubject.create(1)
    private val app = context.applicationContext as RPApplication
    private val environment = app.component()?.environment()
    //private val environment = Environment.builder().build()

     init {
        this.userInformationSubscription.subscribe { userInfo ->
            environment?.currentUser()?.setCurrentUser(userInfo)
        }
    }

    fun getUserInformationSubscription(): Observable<UserInformationResult> {
        return this.userInformationSubscription
    }

    fun updateCurrentUser(citizen: Citizen) {
        val currentUser = environment?.currentUser()?.getCurrentUser()
        if(currentUser!=null){
            val  userInfo = UserInformationResult(currentUser.userId, citizen.identityNumber,
            citizen.firstName, citizen.lastName, citizen.getBirthDateAsString(),
            currentUser.userEmail, currentUser.userPhoneNumber, currentUser.userPhoneVerified,
                currentUser.userEmailVerified, null, currentUser.rewards,
                currentUser.paymentMethods)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUser(paymentMethod: PaymentMethod){
        val currentUser = environment?.currentUser()?.getCurrentUser()
        if(currentUser!=null){
            var paymentMethods = currentUser.paymentMethods.toMutableList()
            paymentMethods.add(paymentMethod)
            val userInfo = UserInformationResult(currentUser.userId, currentUser.citizenId,
                currentUser.userFirstName, currentUser.userLastName, currentUser.userBirthDate,
                currentUser.userEmail, currentUser.userPhoneNumber, currentUser.userPhoneVerified,
                currentUser.userEmailVerified, null, currentUser.rewards, paymentMethods)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUser(user: UserInformationResult){
        this.userInformationSubscription.onNext(user)
    }

    fun updateCurrentUser(email: String) {
        val currentUser = environment?.currentUser()?.getCurrentUser()
        if(currentUser!=null){
            val userInfo = UserInformationResult(currentUser.userId,currentUser.citizenId, currentUser.userFirstName,
                currentUser.userLastName, currentUser.userBirthDate, email, currentUser.userPhoneNumber,
                currentUser.userPhoneVerified,currentUser.userEmailVerified, null, currentUser.rewards,
                currentUser.paymentMethods)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUser( isVerify: Boolean) {
        val currentUser = environment?.currentUser()?.getCurrentUser()
        if(currentUser!=null){
            val userInfo = UserInformationResult(currentUser.userId, currentUser.citizenId,currentUser.userFirstName,
                currentUser.userLastName, currentUser.userBirthDate, currentUser.userEmail, currentUser.userPhoneNumber,
                currentUser.userPhoneVerified, isVerify, null, currentUser.rewards, currentUser.paymentMethods)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

}