package com.zimplifica.redipuntos.services

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject

@SuppressLint("CheckResult")
class GlobalState(val context: Context){
    private var userInformationSubscription  = BehaviorSubject.create<UserInformationResult>()
    private var notificationsSubscription : BehaviorSubject<List<ServerEvent>>
    private var actionableEvents : PublishSubject<ServerEvent>
    //private val app = context.applicationContext as RPApplication
    //private val environment = app.component()?.environment()
    //private val environment = Environment.builder().build()

     init {
         this.notificationsSubscription = BehaviorSubject.create()
         this.actionableEvents = PublishSubject.create()
        this.userInformationSubscription.subscribe { userInfo ->
            Log.e("GlobalState","  "+userInfo.userPhoneNumber+userInfo.citizenId)
            //environment?.currentUser()?.setCurrentUser(userInfo)
            CurrentUser.setCurrentUser(userInfo)
        }
    }

    fun getUserInformationSubscription(): Observable<UserInformationResult> {
        return this.userInformationSubscription
    }

    fun updateCurrentUser(citizen: Citizen) {
        //val currentUser = environment?.currentUser()?.getCurrentUser()
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            Log.e("GlobalState","Citizen ww"+citizen.identityNumber)
            val  userInfo = UserInformationResult(currentUser.userId, citizen.identityNumber,
            citizen.firstName, citizen.lastName, citizen.getBirthDateAsString(),
            currentUser.userEmail, currentUser.userPhoneNumber, currentUser.userPhoneVerified,
                currentUser.userEmailVerified, null, currentUser.rewards,
                currentUser.paymentMethods, currentUser.status, currentUser.gender, currentUser.address)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUser(paymentMethod: PaymentMethod){
        //val currentUser = environment?.currentUser()?.getCurrentUser()
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            var paymentMethods = currentUser.paymentMethods.toMutableList()
            paymentMethods.add(paymentMethod)
            val userInfo = UserInformationResult(currentUser.userId, currentUser.citizenId,
                currentUser.userFirstName, currentUser.userLastName, currentUser.userBirthDate,
                currentUser.userEmail, currentUser.userPhoneNumber, currentUser.userPhoneVerified,
                currentUser.userEmailVerified, null, currentUser.rewards, paymentMethods, currentUser.status,
                currentUser.gender, currentUser.address)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUser(user: UserInformationResult){
        this.userInformationSubscription.onNext(user)
    }

    fun updateCurrentUser(email: String) {
        //val currentUser = environment?.currentUser()?.getCurrentUser()
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            val userInfo = UserInformationResult(currentUser.userId,currentUser.citizenId, currentUser.userFirstName,
                currentUser.userLastName, currentUser.userBirthDate, email, currentUser.userPhoneNumber,
                currentUser.userPhoneVerified,currentUser.userEmailVerified, null, currentUser.rewards,
                currentUser.paymentMethods, currentUser.status,currentUser.gender, currentUser.address)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUser( isVerify: Boolean) {
        //val currentUser = environment?.currentUser()?.getCurrentUser()
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            val userInfo = UserInformationResult(currentUser.userId, currentUser.citizenId,currentUser.userFirstName,
                currentUser.userLastName, currentUser.userBirthDate, currentUser.userEmail, currentUser.userPhoneNumber,
                currentUser.userPhoneVerified, isVerify, null, currentUser.rewards, currentUser.paymentMethods, currentUser.status,
                currentUser.gender, currentUser.address)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUserDelete(withouPaymentMethod : PaymentMethod){
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            var paymentMethods = currentUser.paymentMethods.toMutableList()
            paymentMethods.removeAll{ it.cardId == withouPaymentMethod.cardId}
            val userInfo = UserInformationResult(currentUser.userId, currentUser.citizenId,currentUser.userFirstName,
                currentUser.userLastName, currentUser.userBirthDate, currentUser.userEmail, currentUser.userPhoneNumber,
                currentUser.userPhoneVerified, currentUser.userEmailVerified, null, currentUser.rewards, paymentMethods, currentUser.status,
                currentUser.gender, currentUser.address)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUseerStatus(status : UserStatus){
        val currentUser =CurrentUser.getCurrentUser()
        if (currentUser!=null){
            val userInfo = UserInformationResult(currentUser.userId, currentUser.citizenId,currentUser.userFirstName,
                currentUser.userLastName, currentUser.userBirthDate, currentUser.userEmail, currentUser.userPhoneNumber,
                currentUser.userPhoneVerified, currentUser.userEmailVerified, null, currentUser.rewards, currentUser.paymentMethods, status,
                currentUser.gender, currentUser.address)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    //Events and Notifications global state
    fun registerServerSubscriptionEventWith(element: ServerEvent) {
        val oldArray = this.notificationsSubscription.value.toMutableList()
        oldArray.add(0,element)
        this.notificationsSubscription.onNext(oldArray)
    }

    fun registerServerSubscriptionEventWith(array: List<ServerEvent>) {
        this.notificationsSubscription.onNext(array)
    }

    fun getNotificationsSubscription() : Observable<List<ServerEvent>> {
        return this.notificationsSubscription
    }

    fun registerActionableEvent(element: ServerEvent){
        actionableEvents.onNext(element)
    }

    fun getActionableEventSubscription() : Observable<ServerEvent> {
        return this.actionableEvents
    }
}