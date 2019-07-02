package com.zimplifica.redipuntos.services

import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.useCases.UserUseCase
import com.zimplifica.domain.entities.*
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

    fun fetchTransactions(username: String,useCache: Boolean) : Observable<Result<TransactionsResult>> {
        return userUseCase.fetchTransactions(username, useCache)
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

}