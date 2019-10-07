package com.zimplifica.domain.useCases

import com.zimplifica.domain.entities.*
import io.reactivex.Observable

interface UserUseCase{
    fun getUserInformation(useCache : Boolean) :Observable<Result<UserInformationResult>>
    fun fetchTransactions(useCache: Boolean,nextToken: String?, limit: Int?) : Observable<Result<TransactionsResult>>
    //fun updateUserInfo(citizen: CitizenInput) : Observable<Result<Citizen>>
    fun addPaymentMethod(paymentMethod: PaymentMethodInput ) : Observable<Result<PaymentMethod>>
    fun disablePaymentMethod(cardId: String) : Observable<Result<PaymentMethod>>
    fun checkoutPayloadSitePay(amount: Float, vendorId: String) : Observable<Result<PaymentPayload>>
    fun requestPayment(requestPaymentInput: RequestPaymentInput) : Observable<Result<Transaction>>
    fun getVendorInformation(vendorId: String) : Observable<Result<Vendor>>
    fun getCommerces(limit: Int?, skip: Int?, categoryId: String?, textSearch: String?) : Observable<Result<CommercesResult>>
    fun fetchCategories() : Observable<Result<List<Category>>>
    fun getTransactionById(id: String) : Observable<Result<Transaction>>
    fun registPushNotificationToken(token : String, userId : String) : Observable<Result<String>>
    //fun initIdentitiyVerification() : Observable<Result<Boolean>>
    fun subscribeToServerEvents(user: String) : RediSubscription
    fun fetchNotifications(nextToken: String?, limit: Int?) :  Observable<Result<List<ServerEvent>>>
    fun updateNotificationStatus(id: String) : Observable<Result<Boolean>>
    fun updateFavoriteMerchant(merchantId: String, isFavorite: Boolean) : Observable<Result<Boolean>>
    fun reviewMerchant(rateCommerceModel: RateCommerceModel) : Observable<Result<Boolean>>
    fun createPin(securityCode: SecurityCode) : Observable<Result<Boolean>>
    fun verifyPin(securityCode: SecurityCode) : Observable<Result<Boolean>>
    fun verifyPhoneNumber() : Observable<Result<Boolean>>
    fun updatePin(securityCode: SecurityCode) : Observable<Result<Boolean>>
    fun changePassword(changePasswordModel: ChangePasswordModel) : Observable<Result<Boolean>>
    fun initForgotPassword(phoneNumber: String) : Observable<Result<Boolean>>
    fun confirmForgotPassword(forgotPasswordModel: ForgotPasswordModel) : Observable<Result<Boolean>>
}