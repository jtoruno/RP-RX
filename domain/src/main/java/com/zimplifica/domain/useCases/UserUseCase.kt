package com.zimplifica.domain.useCases

import com.zimplifica.domain.entities.*
import io.reactivex.Observable

interface UserUseCase{
    fun getUserInformation(useCache : Boolean) :Observable<Result<UserInformationResult>>
    fun fetchTransactions(useCache: Boolean,nextToken: String?, limit: Int?) : Observable<Result<TransactionsResult>>
    fun updateUserInfo(citizen: CitizenInput) : Observable<Result<Citizen>>
    fun addPaymentMethod(paymentMethod: PaymentMethodInput ) : Observable<Result<PaymentMethod>>
    fun checkoutPayloadSitePay(username: String, amount: Float, vendorId: String, description: String) : Observable<Result<PaymentPayload>>
    fun requestPayment(requestPaymentInput: RequestPaymentInput) : Observable<Result<Transaction>>
    fun getVendorInformation(vendorId: String) : Observable<Result<Vendor>>
    fun disablePaymentMethod(owner: String, cardId: String) : Observable<Result<PaymentMethod>>
    fun getTransactionById(id: String) : Observable<Result<Transaction>>

    fun getCommerces(limit: Int?, skip: Int?, categoryId: String?, textSearch: String?) : Observable<Result<CommercesResult>>
    //fun searchCommerces(searchText: String) : Observable<Result<CommercesResult>>
    fun fetchCategories() : Observable<Result<List<Category>>>
    //fun filterCommercesByCategory(categoryId: String) : Observable<Result<CommercesResult>>
    fun registPushNotificationToken(token : String, userId : String) : Observable<Result<String>>
    fun initIdentitiyVerification() : Observable<Result<Boolean>>
}