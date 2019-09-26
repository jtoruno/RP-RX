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
    private var userStateSubscription : BehaviorSubject<UserStateResult>
    private var userInformationSubscription  = BehaviorSubject.create<UserInformationResult>()
    private var notificationsSubscription : BehaviorSubject<List<ServerEvent>>
    private var actionableEvents : PublishSubject<ServerEvent>
    private var paymentsSubscription : PublishSubject<Transaction>
    private var paymentMethodsSubscription : PublishSubject<PaymentMethod>
    //private val app = context.applicationContext as RPApplication
    //private val environment = app.component()?.environment()
    //private val environment = Environment.builder().build()

     init {
         this.userStateSubscription = BehaviorSubject.createDefault(UserStateResult.signedOut)
         this.paymentMethodsSubscription = PublishSubject.create()
         this.notificationsSubscription = BehaviorSubject.createDefault(mutableListOf())
         this.actionableEvents = PublishSubject.create()
         this.paymentsSubscription = PublishSubject.create()
         this.userInformationSubscription.subscribe { userInfo ->
            CurrentUser.setCurrentUser(userInfo)
         }
    }

    fun getUserInformationSubscription(): Observable<UserInformationResult> {
        return this.userInformationSubscription
    }

    fun getPaymentMethodSubscription(): Observable<PaymentMethod>{
        return this.paymentMethodsSubscription
    }

    fun updateCurrentUser(paymentMethod: PaymentMethod){
        //val currentUser = environment?.currentUser()?.getCurrentUser()
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            var paymentMethods = currentUser.paymentMethods.toMutableList()
            paymentMethods.add(paymentMethod)
            val userInfo = UserInformationResult(currentUser.id, currentUser.nickname,
                currentUser.userEmail, currentUser.userPhoneNumber, currentUser.userPhoneVerified,
                currentUser.userEmailVerified,  currentUser.rewards, paymentMethods, currentUser.securityCodeCreated)
            this.userInformationSubscription.onNext(userInfo)
            this.paymentMethodsSubscription.onNext(paymentMethod)
        }
    }

    fun updateCurrentUser(user: UserInformationResult){
        this.userInformationSubscription.onNext(user)
    }

    fun updateCurrentUser(email: String) {
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            val userInfo = UserInformationResult(currentUser.id, currentUser.nickname,
                email, currentUser.userPhoneNumber, currentUser.userPhoneVerified,
                currentUser.userEmailVerified,  currentUser.rewards, currentUser.paymentMethods, currentUser.securityCodeCreated)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUser( isVerify: Boolean) {
        //val currentUser = environment?.currentUser()?.getCurrentUser()
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            val userInfo = UserInformationResult(currentUser.id, currentUser.nickname,
                currentUser.userEmail, currentUser.userPhoneNumber, currentUser.userPhoneVerified,
                isVerify,  currentUser.rewards, currentUser.paymentMethods, currentUser.securityCodeCreated)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUserDelete(withouPaymentMethod : PaymentMethod){
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            val paymentMethods = currentUser.paymentMethods.toMutableList()
            paymentMethods.removeAll{ it.cardId == withouPaymentMethod.cardId}
            val userInfo = UserInformationResult(currentUser.id, currentUser.nickname,
                currentUser.userEmail, currentUser.userPhoneNumber, currentUser.userPhoneVerified,
                currentUser.userEmailVerified,  currentUser.rewards, paymentMethods, currentUser.securityCodeCreated)
            this.userInformationSubscription.onNext(userInfo)
        }
    }

    fun updateCurrentUserSecurityCode(securityCodeCreated: Boolean){
        val currentUser =CurrentUser.getCurrentUser()
        if(currentUser!=null){
            val userInfo = UserInformationResult(currentUser.id, currentUser.nickname,
                currentUser.userEmail, currentUser.userPhoneNumber, currentUser.userPhoneVerified,
                currentUser.userEmailVerified,  currentUser.rewards, currentUser.paymentMethods, securityCodeCreated)
            this.userInformationSubscription.onNext(userInfo)
        }

    }

    fun registerNewPayment(transaction: Transaction){
        paymentsSubscription.onNext(transaction)
    }

    fun getPaymentSubscription() : Observable<Transaction>{
        return this.paymentsSubscription
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

    fun updateUserState(state : UserStateResult){
        return this.userStateSubscription.onNext(state)
    }

    fun getUserStateSubscription() : Observable<UserStateResult> {
        return userStateSubscription
    }

    fun getCurrentUserState() : UserStateResult {
        return userStateSubscription.value
    }
}