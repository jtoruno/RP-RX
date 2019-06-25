package com.zimplifica.domain.useCases

import com.zimplifica.domain.entities.*
import io.reactivex.Observable

interface UserUseCase{
    fun getUserInformation(useCache : Boolean) :Observable<Result<UserInformationResult>>
    fun fetchTransactions(username: String, useCache: Boolean) : Observable<Result<TransactionsResult>>
    fun updateUserInfo(citizen: CitizenInput) : Observable<Result<Citizen>>
    fun addPaymentMethod(paymentMethod: PaymentMethodInput ) : Observable<Result<PaymentMethod>>
    fun checkoutPayloadSitePay(username: String, amount: Float, vendorId: String, description: String) : Observable<Result<PaymentPayload>>
    fun requestPayment(requestPaymentInput: RequestPaymentInput) : Observable<Result<Transaction>>
    fun getVendorInformation(vendorId: String) : Observable<Result<Vendor>>
    fun disablePaymentMethod(owner: String, cardId: String) : Observable<Result<PaymentMethod>>
}