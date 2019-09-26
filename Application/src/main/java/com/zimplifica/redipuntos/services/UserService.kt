package com.zimplifica.redipuntos.services

import android.annotation.SuppressLint
import android.util.Log
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.useCases.UserUseCase
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.models.ServerSubscription
import io.reactivex.Observable

class UserService(private val userUseCase: UserUseCase, private val state: GlobalState) {

    fun getUserInformation(useCache: Boolean) : Observable<Result<UserInformationResult>> {
        return userUseCase.getUserInformation(useCache)
            .doOnNext { result ->
                when(result){
                    is Result.success -> {
                        this.state.updateCurrentUser(result.value!!)
                    }
                    else -> return@doOnNext
                }
            }
    }

    fun getCommerces(limit: Int?, skip: Int?, categoryId: String?, textSearch: String?) : Observable<Result<CommercesResult>> {
        return userUseCase.getCommerces(limit,skip,categoryId,textSearch)
    }

    fun fetchTransactions(useCache: Boolean,nextToken: String?, limit: Int?) : Observable<Result<TransactionsResult>> {
        return userUseCase.fetchTransactions(useCache,nextToken,limit)
    }

    fun addPaymentMethod(paymentMethod: PaymentMethodInput) : Observable<Result<PaymentMethod>> {
        return userUseCase.addPaymentMethod(paymentMethod)
            .doOnNext {result ->
                when(result){
                    is Result.success -> {
                        this.state.updateCurrentUser(result.value!!)
                    }
                    else -> return@doOnNext
                }
            }
    }

    fun disablePaymentMethod(cardId: String) : Observable<Result<PaymentMethod>>{
        return userUseCase.disablePaymentMethod(cardId)
            .doOnNext {result ->
                when(result){
                    is Result.success -> {
                        this.state.updateCurrentUserDelete(result.value!!)
                    }
                    else -> return@doOnNext
                }
            }
    }

    fun checkoutPayloadSitePay(amount: Float, vendorId: String) : Observable<Result<PaymentPayload>> {
        return userUseCase.checkoutPayloadSitePay(amount,vendorId)
    }

    fun requestPayment(requestPaymentInput: RequestPaymentInput) : Observable<Result<Transaction>> {
        return userUseCase.requestPayment(requestPaymentInput)
            .doOnNext { result ->
                if (result==null) return@doOnNext
                when(result){
                    is Result.success ->{
                        this.state.registerNewPayment(result.value!!)
                    }else -> return@doOnNext
                }
            }
    }

    fun getVendorInformation(vendorId: String) : Observable<Result<Vendor>> {
        return userUseCase.getVendorInformation(vendorId)
    }

    fun fetchCategories() : Observable<Result<List<Category>>> {
        return userUseCase.fetchCategories()
    }

    fun getTransactionById(id: String) : Observable<Result<Transaction>> {
        return userUseCase.getTransactionById(id)
    }

    fun registPushNotificationToken(token : String, userId : String) : Observable<Result<String>>{
        return userUseCase.registPushNotificationToken(token, userId)
    }

    fun updateNotificationStatus(id: String) : Observable<Result<Boolean>>{
        return userUseCase.updateNotificationStatus(id)
    }

    fun fetchNotifications(nextToken: String?, limit: Int?) : Observable<Result<List<ServerEvent>>> {
        return userUseCase.fetchNotifications(nextToken,limit)
    }

    @SuppressLint("CheckResult")
    fun initServerSubscription(userId : String){
        val subscription = userUseCase.subscribeToServerEvents(userId)
        subscription
            .subscribeToEvents()
            .filter { !it.isFail() }
            .map { it.successValue() }
            .subscribe { event ->
                if(event==null) {return@subscribe}
                this.state.registerServerSubscriptionEventWith(event)
                if(event.actionable && !event.triggered){
                    updateNotificationStatus(event.id)
                        .map { Unit }
                        .subscribe{
                            this.state.registerActionableEvent(event)
                        }
                }
            }

        subscription
            .subscribeToBaseEvents()
            .filter { !it.isFail() }
            .map { it.successValue() }
            .subscribe { notifications ->
                if(notifications==null) {return@subscribe}
                this.state.registerServerSubscriptionEventWith(notifications)
                notifications.filter {n ->
                    return@filter n.actionable && !n.triggered
                }.forEach { n ->
                    updateNotificationStatus(n.id)
                        .map { Unit }
                        .subscribe {
                            this.state.registerActionableEvent(n)
                        }
                }
            }

        //updateServerSubscription(this)
        ServerSubscription.updateServerSubscription(subscription)
    }

    fun updateFavoriteMerchant(merchantId: String, isFavorite: Boolean) : Observable<Result<Boolean>> {
        return userUseCase.updateFavoriteMerchant( merchantId,isFavorite)
    }

    fun reviewMerchant(rateCommerceModel: RateCommerceModel) : Observable<Result<Boolean>> {
        return userUseCase.reviewMerchant( rateCommerceModel)
    }

    fun createPin(securityCode: SecurityCode) : Observable<Result<Boolean>> {
        return userUseCase.createPin( securityCode)
            .doOnNext {result ->
                when(result){
                    is Result.success -> {
                        this.state.updateCurrentUserSecurityCode(result.value?:false)
                    }
                    else -> return@doOnNext
                }
            }
    }

    fun verifyPin(securityCode: SecurityCode) : Observable<Result<Boolean>> {
        return userUseCase.verifyPin(securityCode)
    }

    fun verifyPhoneNumber() : Observable<Result<Boolean>> {
        return userUseCase.verifyPhoneNumber()
    }

    fun updatePin(securityCode: SecurityCode) : Observable<Result<Boolean>> {
        return userUseCase.updatePin(securityCode)
    }

    fun changePassword(changePasswordModel: ChangePasswordModel) : Observable<Result<Boolean>> {
        return userUseCase.changePassword(changePasswordModel)
    }

    fun initForgotPassword(phoneNumber: String) : Observable<Result<Boolean>> {
        return Observable.never()
    }

    /*
    fun confirmForgotPassword(forgotPasswordModel: ForgotPasswordModel) : Observable<Result<Boolean>> {
        return userUseCase.confirmForgotPassword(forgotPasswordModel)
    }*/


    //Global State Subscriptions

    fun getUserInformationSubscription() : Observable<UserInformationResult>{
        return state.getUserInformationSubscription()
    }

    fun getNotificationsSubscription() : Observable<List<ServerEvent>>{
        return state.getNotificationsSubscription()
    }

    fun getPaymentsSubscription() : Observable<Transaction> {
        return state.getPaymentSubscription()
    }

    fun registerNewPayment(transaction: Transaction) {
        state.registerNewPayment(transaction)
    }

    fun getActionableEventSubscription() : Observable<ServerEvent>{
        return state.getActionableEventSubscription()
    }

    fun getPaymentMethodsSubscription() : Observable<PaymentMethod>{
        return state.getPaymentMethodSubscription()
    }
}