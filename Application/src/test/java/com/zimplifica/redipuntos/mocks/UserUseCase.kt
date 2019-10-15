package com.zimplifica.redipuntos.mocks

import com.zimplifica.domain.entities.*
import com.zimplifica.domain.useCases.UserUseCase
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.Exception

class UserUseCase : UserUseCase{
    override fun initForgotPassword(phoneNumber: String): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            if(phoneNumber == "+50688888888"){
                single.onSuccess(Result.failure(Exception()))
            }else{
                single.onSuccess(Result.success(true))
            }
        }
        return single.toObservable()
    }

    override fun confirmForgotPassword(forgotPasswordModel: ForgotPasswordModel): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            if(forgotPasswordModel.phoneNumber  == "+50688888888" || forgotPasswordModel.password == "123abc#"){
                single.onSuccess(Result.failure(Exception()))
            }else{
                single.onSuccess(Result.success(true))
            }
        }
        return single.toObservable()
    }

    override fun updateFavoriteMerchant(merchantId: String, isFavorite: Boolean): Observable<Result<Boolean>> {
        return Observable.create<Result<Boolean>> { observer ->
            if(merchantId == "1234"){
                observer.onNext(Result.failure(Exception()))
                observer.onComplete()
            }else{
                observer.onNext(Result.success(true))
                observer.onComplete()
            }
        }
    }

    override fun reviewMerchant(rateCommerceModel: RateCommerceModel): Observable<Result<Boolean>> {
        return Observable.create<Result<Boolean>> { observer ->
            if(rateCommerceModel.commerceName == "WrongCommerce"){
                observer.onNext(Result.failure(Exception()))
                observer.onComplete()
            }else{
                observer.onNext(Result.success(true))
                observer.onComplete()
            }
        }
    }

    override fun createPin(securityCode: SecurityCode): Observable<Result<Boolean>> {
        return Observable.create<Result<Boolean>> { observer ->
            observer.onNext(Result.success(true))
            observer.onComplete()
        }
    }

    override fun verifyPin(securityCode: SecurityCode): Observable<Result<Boolean>> {
        return Observable.create<Result<Boolean>> { observer ->
            if(securityCode.pin == "1234"){
                observer.onNext(Result.failure(Exception()))
                observer.onComplete()
            }else{
                observer.onNext(Result.success(true))
                observer.onComplete()
            }
        }
    }

    override fun verifyPhoneNumber(): Observable<Result<Boolean>> {
        return Observable.create<Result<Boolean>> { observer ->
            observer.onNext(Result.success(true))
            observer.onComplete()
        }    }

    override fun updatePin(securityCode: SecurityCode): Observable<Result<Boolean>> {
        return Observable.create<Result<Boolean>> { observer ->
            observer.onNext(Result.success(true))
            observer.onComplete()
        }
    }

    override fun changePassword(changePasswordModel: ChangePasswordModel): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            if(changePasswordModel.verificationCode == "666666"){
                single.onSuccess(Result.failure(Exception()))
            }else{
                single.onSuccess(Result.success(true))
            }
        }
        return single.toObservable()
    }


    override fun subscribeToServerEvents(user: String): RediSubscription {
        return ServerSubscriptionTest(user)
    }

    override fun fetchNotifications(nextToken: String?, limit: Int?): Observable<Result<List<ServerEvent>>> {
        return Observable.never()
    }

    override fun updateNotificationStatus(id: String): Observable<Result<Boolean>> {
        return Observable.never()
    }

    override fun registPushNotificationToken(token: String, userId : String): Observable<Result<String>> {
        val single = Single.create<Result<String>> create@{ single ->
            if(token.isNotEmpty()){
                single.onSuccess(Result.success(token))
            }else{
                single.onSuccess(Result.failure(Exception()))
            }
        }
        return single.toObservable()
    }


    override fun disablePaymentMethod(cardId: String): Observable<Result<PaymentMethod>> {
        val single = Single.create<Result<PaymentMethod>> create@{ single ->
            if(cardId == "1234"){
                val paymentM = PaymentMethod("XXXXXX-XXXXX-XXX", "9944", "10/20","VISA")
                single.onSuccess(Result.success(paymentM))
            }
            else{
                val error = Exception("disablePaymentMethod")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun getUserInformation(useCache: Boolean): Observable<Result<UserInformationResult>> {
        val single = Single.create<Result<UserInformationResult>> create@{
            it.onSuccess(Result.success(userInformationMock()))
        }
        return single.toObservable()
    }

    override fun fetchTransactions(useCache: Boolean,nextToken: String?, limit: Int?): Observable<Result<TransactionsResult>> {
        val single = Single.create<Result<TransactionsResult>> create@{ single ->
            if(!useCache){
                val cardDetail = CardDetail("12312412512","2324", "visa")
                val wayToPay = WayToPay(4000.0,cardDetail,4000.0)
                val sitePaymentItem = SitePaymentItem("","Test",2000.0,"231421agewg24-2131-fwawefa-f2332",
                    "Manpuku Sushi")
                val transactionDetail = TransactionDetail(TransactionType.directPayment.toString(), 5550.0,"231421agewg24-2131-fwawefa-f2332","Manpuku Sushi")
                val transactions = mutableListOf<Transaction>()
                transactions.add(Transaction("1234", "11-11-2019", "debit", transactionDetail, 0.0, 50.0,6500.0,6500.0,25.0,TransactionStatus.success, wayToPay,""))
                transactions.add(Transaction("4321","11-11-2019","debit",transactionDetail, 0.0,50.0,20500.0,20500.0,40.0,TransactionStatus.fail,wayToPay,""))
                val transactionsResult = TransactionsResult(transactions, null)
                single.onSuccess(Result.success(transactionsResult))
            } else {
                val error = Exception("")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun addPaymentMethod(paymentMethod: PaymentMethodInput): Observable<Result<PaymentMethod>> {
        val single = Single.create<Result<PaymentMethod>> create@{ single ->
            if(paymentMethod.cardNumber == "4539169425022428"){
                val paymentM = PaymentMethod("XXXXXX-XXXXX-XXX","9944","10/20","VISA")
                single.onSuccess(Result.success(paymentM))
            }
            else{
                val error = Exception("addPaymentMethod")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun checkoutPayloadSitePay(
        amount: Float,
        vendorId: String
    ): Observable<Result<PaymentPayload>> {
        val single = Single.create<Result<PaymentPayload>> create@{ single ->
            if (vendorId == "7120-39345-1023841023-123434"){
                val item = Item("",5555.5)
                val order = Order("3c288f1b-e95f-40a2-8f53-40b61d356156", item, 0.0, 5555.5,5555.5,5555.5,10,13)
                val paymentMethods = mutableListOf<PaymentMethod>()
                paymentMethods.add(PaymentMethod("1234321412341234","1234","","visa"))
                val paymentPayload = PaymentPayload(1000.0,order,paymentMethods)
                single.onSuccess(Result.success(paymentPayload))
            }else{
                val error = Exception("")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun requestPayment(requestPaymentInput: RequestPaymentInput): Observable<Result<Transaction>> {
        val single = Single.create<Result<Transaction>> create@{ single ->
            if(requestPaymentInput.orderId == "3c288f1b-e95f-40a2-8f53-40b61d356156"){
                val wayToPay = WayToPay(4000.0,null, 4000.0)
                val transactionDetail = TransactionDetail("debit", 2113.0,  "1234532",  "Zimplifica")
                val transaction = Transaction("123451234",  "1235123612",  "debit",  transactionDetail,  0.0,  0.0,  0.0,  2113.0,  0.0,  TransactionStatus.success, wayToPay,  "")
                single.onSuccess(Result.success(transaction))
            }else{
                val error = Exception("")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun getVendorInformation(vendorId: String): Observable<Result<Vendor>> {
        val single = Single.create<Result<Vendor>> create@{ single ->
            if(vendorId == "7120-39345-1023841023-123434"){
                val vendor = Vendor("7120-39345-1023841023-123434","Manpuku Sushi","Jaco")
                single.onSuccess(Result.success(vendor))
            }else{
                val error = Exception("")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun getCommerces(limit: Int?, skip: Int?, categoryId: String?, textSearch: String?): Observable<Result<CommercesResult>> {
        val single = Single.create<Result<CommercesResult>> create@{ single ->
            var commercesResult = CommercesResult(mutableListOf(), 0)
            if(textSearch == "Test"){
                single.onSuccess(Result.failure(Exception()))
            }else if(categoryId == "54321"){
                val commerces = mutableListOf<Commerce>()
                commerces.add(Commerce("12345","Pizza Hut","","www.pizzahut.com","www.facebook.com/pizzahut","+50688889999","www.instagram.com/pizzahut","Food",
                    mutableListOf(), "", "", "",  Offer( 10),  10,true))
                commerces.add(Commerce("54321`","Pizza Hut 2",  "","www.pizzahut2.com","www.facebook.com/pizzahut2", "+50688889999",  "www.instagram.com/pizzahut2", "Food",
                    mutableListOf(), "",  "",  "",  Offer( 10),  10,true))
                commercesResult = CommercesResult(commerces,2)
            }else{
                val commerces = mutableListOf<Commerce>()
                commerces.add(Commerce("12345","Pizza Hut","","www.pizzahut.com","www.facebook.com/pizzahut","+50688889999","www.instagram.com/pizzahut","Food",
                    mutableListOf(), "", "", "",  Offer( 10),  10,true))
                commercesResult = CommercesResult(commerces,8)
            }

            single.onSuccess(Result.success(commercesResult))
        }
        return single.toObservable()
    }


    override fun fetchCategories(): Observable<Result<List<Category>>> {
        val single = Single.create<Result<List<Category>>> create@{ single ->
            val categoryResult = mutableListOf<Category>()
            categoryResult.add(Category("1234","Food",""))
            categoryResult.add(Category("4321","Health",""))
            single.onSuccess(Result.success(categoryResult))
        }
        return single.toObservable()
    }

    override fun getTransactionById(id: String): Observable<Result<Transaction>> {
        return Observable.never()
    }

}