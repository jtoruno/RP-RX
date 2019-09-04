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

    fun getUserInformationSubscription() : Observable<UserInformationResult>{
        return state.getUserInformationSubscription()
    }

    fun fetchTransactions(useCache: Boolean,nextToken: String?, limit: Int?) : Observable<Result<TransactionsResult>> {
        return userUseCase.fetchTransactions(useCache,nextToken,limit)
    }

    fun updateUserInfo(citizen: CitizenInput) : Observable<Result<Citizen>> {
        return userUseCase.updateUserInfo(citizen)
            .doOnNext { result ->
                when(result){
                    is Result.success -> {
                        this.state.updateCurrentUser(result.value!!)
                    }
                    else -> return@doOnNext
                }
            }
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

    fun checkoutPayloadSitePay(username: String, amount: Float, vendorId: String, description: String) : Observable<Result<PaymentPayload>> {
        return userUseCase.checkoutPayloadSitePay(username,amount,vendorId,description)
    }

    fun requestPayment(requestPaymentInput: RequestPaymentInput) : Observable<Result<Transaction>> {
        return userUseCase.requestPayment(requestPaymentInput)
    }

    fun getVendorInformation(vendorId: String) : Observable<Result<Vendor>> {
        return userUseCase.getVendorInformation(vendorId)
    }

    fun disablePaymentMethod(owner: String, cardId: String) : Observable<Result<PaymentMethod>>{
        return userUseCase.disablePaymentMethod(owner,cardId)
            .doOnNext {result ->
                when(result){
                    is Result.success -> {
                        this.state.updateCurrentUserDelete(result.value!!)
                    }
                    else -> return@doOnNext
                }
            }
    }

    fun getTransactionById(id: String) : Observable<Result<Transaction>> {
        return userUseCase.getTransactionById(id)
    }

    fun getCommerces(limit: Int?, skip: Int?, categoryId: String?, textSearch: String?) : Observable<Result<CommercesResult>> {
        return userUseCase.getCommerces(limit,skip,categoryId,textSearch)
    }

    /*
    fun searchCommerces(searchText: String) : Observable<Result<CommercesResult>> {
        return userUseCase.searchCommerces(searchText)
    }

    fun filterCommercesByCategory(categoryId: String) : Observable<Result<CommercesResult>> {
        return userUseCase.filterCommercesByCategory(categoryId)
    }*/

    fun fetchCategories() : Observable<Result<List<Category>>> {
        return userUseCase.fetchCategories()
    }

    fun registPushNotificationToken(token : String, userId : String) : Observable<Result<String>>{
        Log.e("Service","token" + token)
        return userUseCase.registPushNotificationToken(token, userId)
    }

    fun initIdentitiyVerification() : Observable<Result<Boolean>>{
        return userUseCase.initIdentitiyVerification()
            .doOnNext { result ->
                when(result){
                    is Result.success -> {
                        val userStatus = UserStatus(VerificationStatus.Verifying,null)
                        this.state.updateCurrentUseerStatus(userStatus)
                    }
                    else -> return@doOnNext
                }
            }
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

    fun getNotificationsSubscription() : Observable<List<ServerEvent>>{
        return state.getNotificationsSubscription()
    }

    fun getPaymentsSubscription() : Observable<Transaction> {
        return state.getPaymentSubscription()
    }

    fun registerNewPayment(transaction: Transaction) {
        state.registerNewPayment(transaction)
    }

}