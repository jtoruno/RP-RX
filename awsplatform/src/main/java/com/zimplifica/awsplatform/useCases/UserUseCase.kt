package com.zimplifica.awsplatform.useCases

import android.text.format.DateUtils
import android.util.Log
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.amazonaws.rediPuntosAPI.*
import com.amazonaws.rediPuntosAPI.type.*
import com.amazonaws.rediPuntosAPI.type.WayToPayInput
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.zimplifica.awsplatform.AppSync.AppSyncClient
import com.zimplifica.awsplatform.AppSync.CacheOperations
import com.zimplifica.awsplatform.AppSync.ServerSubscription
import com.zimplifica.awsplatform.Pinpoint.PinpointClient
import com.zimplifica.awsplatform.Utils.PlatformUtils
import com.zimplifica.domain.entities.*
import com.zimplifica.domain.entities.PaymentMethodInput
import com.zimplifica.domain.entities.RequestPaymentInput
import com.zimplifica.domain.useCases.UserUseCase
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.Exception

class UserUseCase : UserUseCase {

    private val appSyncClientPublic = AppSyncClient.getClient(AppSyncClient.AppSyncClientMode.PUBLIC)
    private val appSyncClient = AppSyncClient.getClient(AppSyncClient.AppSyncClientMode.PRIVATE)
    private val cacheOperations = CacheOperations()

    override fun initForgotPassword(phoneNumber: String): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val input = InitForgotPasswordInput.builder().phoneNumber(phoneNumber).build()
            val mutation = InitForgotPasswordMutation.builder().input(input).build()
            this.appSyncClientPublic!!.mutate(mutation).enqueue(object : GraphQLCall.Callback<InitForgotPasswordMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [initForgotPassword] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<InitForgotPasswordMutation.Data>) {
                    val result = response.data()?.initForgotPassword()
                    if(result!=null){
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun confirmForgotPassword(forgotPasswordModel: ForgotPasswordModel): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val jsonString = forgotPasswordModel.getJson() ?: return@create
            val encryptedData = PlatformUtils.encrypt(jsonString)?.replace("\n","") ?: return@create
            val input = ConfirmForgotPasswordInput.builder().data(encryptedData).build()
            val mutation = ConfirmForgotPasswordMutation.builder().input(input).build()
            this.appSyncClientPublic!!.mutate(mutation).enqueue(object: GraphQLCall.Callback<ConfirmForgotPasswordMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [confirmForgotPassword] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<ConfirmForgotPasswordMutation.Data>) {
                    Log.e("confirmForgotPassword",response.errors().toString())
                    val result = response.data()?.confirmForgotPassword()
                    if(result!=null){
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }


    override fun updateFavoriteMerchant(merchantId: String, isFavorite: Boolean): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val input = UpdateFavoriteMerchantInput.builder().isFavorite(isFavorite).merchantId(merchantId).build()
            val mutation = UpdateFavoriteMerchantMutation.builder()
                .input(input)
                .build()
            this.appSyncClient!!.mutate(mutation).enqueue(object: GraphQLCall.Callback<UpdateFavoriteMerchantMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [updateFavoriteMerchant] Error.",e)
                    single.onSuccess(Result.failure(e))                }

                override fun onResponse(response: Response<UpdateFavoriteMerchantMutation.Data>) {
                    val result = response.data()?.updateFavoriteMerchant()
                    if(result!=null){
                        cacheOperations.updateCommerce( isFavorite, merchantId)
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun reviewMerchant(rateCommerceModel: RateCommerceModel): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val input = ReviewMerchantInput.builder().review(rateCommerceModel.rate.int).transactionId(rateCommerceModel.id).build()
            val mutation = ReviewMerchantMutation.builder().input(input).build()
            this.appSyncClient!!.mutate(mutation).enqueue(object: GraphQLCall.Callback<ReviewMerchantMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [reviewMerchant] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<ReviewMerchantMutation.Data>) {
                    val result = response.data()?.reviewMerchant()
                    if(result !=null){
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun createPin(securityCode: SecurityCode): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val jsonString = securityCode.getJson() ?: return@create
            val encryptedData = PlatformUtils.encrypt(jsonString)?.replace("\n","") ?: return@create
            val input = PinInput.builder().data(encryptedData).build()
            val mutation = CreatePinMutation.builder().input(input).build()
            this.appSyncClient!!.mutate(mutation).enqueue(object: GraphQLCall.Callback<CreatePinMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [createPin] Error.",e)
                    single.onSuccess(Result.failure(e))                }

                override fun onResponse(response: Response<CreatePinMutation.Data>) {
                    val result = response.data()?.createPin()
                    if(result!=null){
                        cacheOperations.updateUserHasSecurityCode(result.success())
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun verifyPin(securityCode: SecurityCode): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val jsonString = securityCode.getJson() ?: return@create
            val encryptedData = PlatformUtils.encrypt(jsonString)?.replace("\n","") ?: return@create
            val input = PinInput.builder().data(encryptedData).build()
            val mutation = VerifyPinMutation.builder().input(input).build()
            this.appSyncClient!!.mutate(mutation).enqueue(object: GraphQLCall.Callback<VerifyPinMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [verifyPin] Error.",e)
                    single.onSuccess(Result.failure(e))                }

                override fun onResponse(response: Response<VerifyPinMutation.Data>) {
                    val result = response.data()?.verifyPin()
                    if(result!=null){
                        cacheOperations.updateUserHasSecurityCode(result.success())
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun verifyPhoneNumber(): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val mutation = VerifyUserPhoneNumberMutation.builder().build()
            this.appSyncClient!!.mutate(mutation).enqueue(object : GraphQLCall.Callback<VerifyUserPhoneNumberMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [verifyPhoneNumber] Error.",e)
                    single.onSuccess(Result.failure(e))                  }

                override fun onResponse(response: Response<VerifyUserPhoneNumberMutation.Data>) {
                    val result = response.data()?.verifyUserPhoneNumber()
                    if(result!=null){
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun updatePin(securityCode: SecurityCode): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val jsonString = securityCode.getJson() ?: return@create
            val encryptedData = PlatformUtils.encrypt(jsonString)?.replace("\n","") ?: return@create
            val input = PinInput.builder().data(encryptedData).build()
            val mutation = UpdatePinMutation.builder().input(input).build()
            this.appSyncClient!!.mutate(mutation).enqueue(object: GraphQLCall.Callback<UpdatePinMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [updatePin] Error.",e)
                    single.onSuccess(Result.failure(e))                }

                override fun onResponse(response: Response<UpdatePinMutation.Data>) {
                    val result = response.data()?.updatePin()
                    if(result!=null){
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun changePassword(changePasswordModel: ChangePasswordModel): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val jsonString = changePasswordModel.toJson() ?: return@create
            val encryptedData = PlatformUtils.encrypt(jsonString)?.replace("\n","") ?: return@create
            val input = ChangePasswordInput.builder().data(encryptedData).build()
            val mutation = ChangePasswordMutation.builder().input(input).build()
            this.appSyncClient!!.mutate(mutation).enqueue(object : GraphQLCall.Callback<ChangePasswordMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [changePassword] Error.",e)
                    single.onSuccess(Result.failure(e))                }

                override fun onResponse(response: Response<ChangePasswordMutation.Data>) {
                    val result = response.data()?.changePassword()
                    if(result!=null){
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }                }

            })
        }
        return single.toObservable()
    }

    override fun disablePaymentMethod(cardId: String): Observable<Result<PaymentMethod>> {
        val single = Single.create<Result<PaymentMethod>> create@{ single ->
            val input = DisablePaymentMethodInput.builder().cardId(cardId).build()
            val mutation = DisablePaymentMethodMutation.builder()
                .input(input)
                .build()
            this.appSyncClient!!.mutate(mutation).enqueue(object : GraphQLCall.Callback<DisablePaymentMethodMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [disablePaymentMethod] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<DisablePaymentMethodMutation.Data>) {
                    val result = response.data()?.disablePaymentMethod()
                    if(result!=null){
                        val paymentMethod = PaymentMethod(result.id(),result.cardNumber(),result.expirationDate(),
                            result.issuer())
                        cacheOperations.addPaymentMethod(paymentMethod)
                        single.onSuccess(Result.success(paymentMethod))
                    }
                    else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }

            })
        }
        return single.toObservable()
    }

    override fun requestPayment(requestPaymentInput: RequestPaymentInput): Observable<Result<Transaction>> {
        val single = Single.create<Result<Transaction>> create@{ single ->
            val wayToPayInput = WayToPayInput.builder()
                .rediPuntos(requestPaymentInput.wayToPay.rediPuntos)
                .creditCardId(requestPaymentInput.wayToPay.creditCardId)
                .creditCardCharge(requestPaymentInput.wayToPay.creditCardCharge)
                .build()
            val input = com.amazonaws.rediPuntosAPI.type.RequestPaymentInput.builder()
                .wayToPay(wayToPayInput)
                .paymentDescription(requestPaymentInput.paymentDescription)
                .orderId(requestPaymentInput.orderId)
                .build()
            val mutation = RequestPaymentMutation.builder()
                .input(input)
                .build()
            this.appSyncClient!!.mutate(mutation).enqueue(object : GraphQLCall.Callback<RequestPaymentMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [requestPayment] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<RequestPaymentMutation.Data>) {

                    val result = response.data()?.requestPayment()
                    Log.e("UserUseCase",response.errors().toString())
                    if(result!=null){
                        var cardDetail : CardDetail ? = null
                        val card = result.wayToPay().creditCard()
                        if(card != null){
                            cardDetail = CardDetail(card.id(),card.cardNumber(),card.issuer())
                        }
                        val way2pay = WayToPay(result.wayToPay().rediPuntos(),cardDetail,result.wayToPay().creditCardCharge())
                        val transactionDetail = TransactionDetail(result.item().type(),result.item().amount(),result.item().vendorId(),result.item().vendorName())
                        var status : TransactionStatus = when(result.status()){
                            "successful" -> TransactionStatus.success
                            "pending" -> TransactionStatus.pending
                            "ValitionFailure" -> TransactionStatus.fail
                            else -> TransactionStatus.fail
                        }
                        val transaction = Transaction(result.id(),result.datetime(),result.transactionType(),transactionDetail,
                            result.fee(),result.tax(), result.subtotal(),result.total(),result.rewards(),status,way2pay,result.paymentDescription())

                        //val requestPayment = RequestPayment(true, "")
                        cacheOperations.updateNewTransactions(transaction)

                        single.onSuccess(Result.success(transaction))
                    }
                    else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun checkoutPayloadSitePay(
        amount: Float,
        vendorId: String
    ): Observable<Result<PaymentPayload>> {
        val single = Single.create<Result<PaymentPayload>> create@{ single ->
            val input = CheckoutPayloadSitePayInput.builder()
                .amount(amount.toDouble())
                .vendorId(vendorId)
                .build()
            val query = GetCheckoutPayloadSitePayQuery.builder()
                .input(input)
                .build()
            this.appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY).enqueue(object : GraphQLCall.Callback<GetCheckoutPayloadSitePayQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [checkoutPayloadSitePay] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetCheckoutPayloadSitePayQuery.Data>) {
                    val result = response.data()?.checkoutPayloadSitePay
                    Log.e("Order",result.toString())
                    if(result!=null){
                        val item = Item(result.order().item().type(), result.order().item().amount())
                        val order = Order(result.order().id(), item,result.order().fee(),result.order().tax(),result.order().subtotal(),result.order().total(),result.order().cashback(),result.order().taxes())

                        val rediPuntos = result.paymentOptions().rediPuntos()
                        val paymentMethods = result.paymentOptions().paymentMethods().map { element ->
                            return@map PaymentMethod(element.id(),element.cardNumber(),element.expirationDate(),element.issuer())
                        }
                        val paymentPayload = PaymentPayload(rediPuntos,order,paymentMethods)
                        single.onSuccess(Result.success(paymentPayload))
                    }
                    else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()

    }

    override fun addPaymentMethod(paymentMethod: PaymentMethodInput): Observable<Result<PaymentMethod>> {
        val single = Single.create<Result<PaymentMethod>> create@{ single ->
            val inputValue = PlatformUtils.encrypt(paymentMethod.toJson())?.replace("\n","")
            val input = com.amazonaws.rediPuntosAPI.type.PaymentMethodInput.builder()
                .data(inputValue?:"")
                .build()
            val mutation = AddPaymentMethodMutation.builder()
                .input(input)
                .build()
            this.appSyncClient!!.mutate(mutation).enqueue(object : GraphQLCall.Callback<AddPaymentMethodMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [addPaymentMethod] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<AddPaymentMethodMutation.Data>) {
                    Log.e("addPaymentMth",response.data().toString())
                    Log.e("addPaymentMth",response.errors().toString())
                    val result = response.data()?.addPaymentMethod()
                    if(result!=null){
                        val paymentMethod = PaymentMethod(result.id(),result.cardNumber(),result.expirationDate(),
                            result.issuer())
                        cacheOperations.addPaymentMethod(paymentMethod)
                        single.onSuccess(Result.success(paymentMethod))
                    }
                    else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun fetchTransactions(useCache: Boolean,nextToken: String?, limit: Int?): Observable<Result<TransactionsResult>> {
        val single = Single.create<Result<TransactionsResult>> create@{ single ->
            val input = GetTransactionsByUserInput.builder().limit(limit).nextToken(nextToken).build()
            val query = GetTransactionsByUserQuery.builder()
                .input(input)
                .build()
            val cachePolicy =  if(useCache){
                AppSyncResponseFetchers.CACHE_FIRST
            }else{
                AppSyncResponseFetchers.NETWORK_ONLY
            }
            this.appSyncClient!!.query(query).responseFetcher(cachePolicy).enqueue(object : GraphQLCall.Callback<GetTransactionsByUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [fetchTransactions] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetTransactionsByUserQuery.Data>) {
                    val result = response.data()?.transactionsByUser
                    //Log.e("fetchTransactions","PaginationVal "+result?.nextToken() + " limit "+ limit)
                    Log.e("UserUseCase","Is from cache ->"+response.fromCache())
                    Log.e("UserUseCase",result.toString())
                    if(result!=null){
                        val items = result.items()
                        val transactions : List<Transaction> = items.map { trx ->
                            var cardDetail : CardDetail ? = null
                            val card = trx.wayToPay().creditCard()
                            if(card != null){
                                cardDetail = CardDetail(card.id(),card.cardNumber(),card.issuer())
                            }
                            val wayToPay = WayToPay(trx.wayToPay().rediPuntos(),cardDetail,trx.wayToPay().creditCardCharge())
                            var transactionDetail = TransactionDetail(trx.item().type(),trx.item().amount(),trx.item().vendorId(),trx.item().vendorName())
                            var status : TransactionStatus = when(trx.status()){
                                "successful" -> TransactionStatus.success
                                "pending" -> TransactionStatus.pending
                                "valitionFailure" -> TransactionStatus.fail
                                else -> TransactionStatus.fail
                            }
                            return@map Transaction(trx.id(), trx.datetime(),trx.transactionType(), transactionDetail,trx.fee(),trx.tax(),trx.subtotal(),trx.total(),trx.rewards(),status, wayToPay,trx.paymentDescription())
                        }
                        val transactionsResult = TransactionsResult(transactions,result.nextToken())
                        single.onSuccess(Result.success(transactionsResult))
                    }
                    else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }

            })

        }
        return single.toObservable()
    }

    override fun getUserInformation(useCache: Boolean): Observable<Result<UserInformationResult>> {
        val single = Single.create<Result<UserInformationResult>> create@{ single->
            val query = GetUserQuery.builder().build()
            val cachePolicy =  if(useCache){
                AppSyncResponseFetchers.CACHE_FIRST
            }else{
                AppSyncResponseFetchers.NETWORK_ONLY
            }
            this.appSyncClient!!.query(query).responseFetcher(cachePolicy).enqueue(object: GraphQLCall.Callback<GetUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [GetUserInformation] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetUserQuery.Data>) {
                    if(response.data()!=null){
                        val user = response.data()!!.user
                        val paymentMethods = response.data()!!.user.paymentMethods().map { p ->
                            return@map PaymentMethod(p.id(),p.cardNumber(),p.expirationDate(),p.issuer())
                        }

                        val userObj = UserInformationResult(user.id(),user.nickname(),user.email(),user.phoneNumber(),user.phoneNumberVerified(),user.emailVerified(),user.rewards(),
                            paymentMethods, user.hasSecurityCode())
                        single.onSuccess(Result.success(userObj))
                    }
                    else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun getVendorInformation(vendorId: String): Observable<Result<Vendor>> {
        val single = Single.create<Result<Vendor>> create@{ single ->
            val input = GetVendorInput.builder().id(vendorId).build()
            val query = GetVendorQuery.builder().input(input).build()
            this.appSyncClient!!.query(query).enqueue(object :GraphQLCall.Callback<GetVendorQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [getVendorInformation] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetVendorQuery.Data>) {
                    val result = response.data()?.vendor
                    Log.e("AWSPLAT",result.toString())
                    if(result!=null){
                        val vendor = Vendor(result.id(),result.name(),result.address())
                        single.onSuccess(Result.success(vendor))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }

            })
        }
        return single.toObservable()
    }

    override fun getTransactionById(id: String): Observable<Result<Transaction>> {
        val single = Single.create<Result<Transaction>> create@{ single ->
            val input = GetTransactionByIdInput.builder().id(id).build()
            val query = GetTransactionByIdQuery.builder().input(input).build()
            this.appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY).enqueue(object:GraphQLCall.Callback<GetTransactionByIdQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [getTransactionById] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetTransactionByIdQuery.Data>) {
                    val result = response.data()?.transactionById
                    Log.e("TransactionById",result.toString())
                    if(result != null){
                        var cardDetail : CardDetail ? = null
                        val card = result.wayToPay().creditCard()
                        if(card != null){
                            cardDetail = CardDetail(card.id(),card.cardNumber(),card.issuer())
                        }
                        val way2pay = WayToPay(result.wayToPay().rediPuntos(),cardDetail,result.wayToPay().creditCardCharge())
                        val transactionDetail = TransactionDetail(result.item().type(),result.item().amount(),result.item().vendorId(),result.item().vendorName())
                        val status : TransactionStatus = when(result.status()){
                            "successful" -> TransactionStatus.success
                            "pending" -> TransactionStatus.pending
                            else -> TransactionStatus.fail
                        }
                        val transaction = Transaction(result.id(),result.datetime(),result.transactionType(),transactionDetail,
                            result.fee(),result.tax(), result.subtotal(),result.total(),result.rewards(),status,way2pay,result.paymentDescription())

                        cacheOperations.updateNewTransactions(newTransaction = transaction)
                        cacheOperations.updateTransactions(withExistingTransaction = transaction)

                        single.onSuccess(Result.success(transaction))

                    }
                    else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun getCommerces(limit: Int?, skip: Int?, categoryId: String?, textSearch: String?): Observable<Result<CommercesResult>> {
        val single = Single.create<Result<CommercesResult>> create@{ single ->
            val input = GetCommercesInput.builder().limit(limit).skip(skip) .categoryId(categoryId).textSearch(textSearch).build()
            val query = GetCommercesQuery.builder().input(input).build()
            this.appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY).enqueue(object:GraphQLCall.Callback<GetCommercesQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [getCommerces] Error.",e)
                    single.onSuccess(Result.failure(e))                }

                override fun onResponse(response: Response<GetCommercesQuery.Data>) {
                    val data = response.data()?.commerces
                    if(data!=null){
                        val commerces = handleTransformCommercesInfo(data.items())
                        val commercesResult = CommercesResult(commerces, data.total())
                        single.onSuccess(Result.success(commercesResult))
                    }
                    else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }

            })
        }
        return single.toObservable()
    }

    override fun fetchCategories(): Observable<Result<List<Category>>> {
        val single = Single.create<Result<List<Category>>> create@{ single ->
            val input = GetCategoriesInput.builder().limit(null).nextToken(null).build()
            val query = GetCategoriesQuery.builder().input(input).build()
            this.appSyncClient!!.query(query).enqueue(object : GraphQLCall.Callback<GetCategoriesQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [fetchCategories] Error.",e)
                    single.onSuccess(Result.failure(e))                }

                override fun onResponse(response: Response<GetCategoriesQuery.Data>) {
                    val data = response.data()?.categories
                    if(data!=null){
                        val list = data.items()
                        val categories = list.map { category ->
                            return@map Category(category.id(),category.name(),category.categoryImage())
                        }
                        single.onSuccess(Result.success(categories))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }

            })
        }
        return single.toObservable()
    }

    override fun registPushNotificationToken(token: String, userId : String): Observable<Result<String>> {
        val single = Single.create<Result<String>> create@{ single ->
            Log.e("UserUseCase", "sucesss " + userId + "token "+ token)
            if (token.isEmpty() || userId.isEmpty()){
                single.onSuccess(Result.failure(Exception()))
            }else{
                val tokenResponse = PinpointClient.setTokenDevice(token, userId)
                single.onSuccess(Result.success(tokenResponse))
            }
        }
        return single.toObservable()
    }

    override fun subscribeToServerEvents(user: String): RediSubscription {
        return ServerSubscription(user)
    }

    override fun fetchNotifications(nextToken: String?, limit: Int?): Observable<Result<List<ServerEvent>>> {
        val single = Single.create<Result<List<ServerEvent>>> create@{ single ->
            val input = GetNotificationsInput.builder().limit(limit).nextToken(nextToken).build()
            val query = GetNotificationsQuery.builder().input(input).build()
            this.appSyncClient!!.query(query).enqueue(object: GraphQLCall.Callback<GetNotificationsQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [FetchNotifications] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetNotificationsQuery.Data>) {
                    val trx = response.data()?.notifications
                    if (trx!=null){
                        val notifications = trx.items().map { n ->
                            val date = DateUtils.getRelativeTimeSpanString(n.createdAt().toLong())
                            return@map ServerEvent(n.id(),n.origin(),n.type(),n.title(),n.message(),date.toString(),n.data(),n.actionable(),n.triggered(),n.hidden())
                        }
                        single.onSuccess(Result.success(notifications))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun updateNotificationStatus(id: String): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val input = UpdateNotificationStatusInput.builder().id(id).build()
            val mutation = UpdateNotificationStatusMutation.builder().input(input).build()
            this.appSyncClient!!.mutate(mutation).enqueue(object: GraphQLCall.Callback<UpdateNotificationStatusMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [UpdateNotificationStatus] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<UpdateNotificationStatusMutation.Data>) {
                    val result = response.data()?.updateNotificationStatus()
                    if (result!=null){
                        cacheOperations.updateNotificationStatus(id)
                        single.onSuccess(Result.success(result.success()))
                    }else{
                        single.onSuccess(Result.failure(Exception()))
                    }
                }
            })
        }
        return single.toObservable()
    }


    private fun handleTransformCommercesInfo(data : MutableList<GetCommercesQuery.Item>) : List<Commerce>{
        var commerces : List<Commerce>
        commerces = data.map { commerce ->

            //Stores
            var commerceStores = mutableListOf<Store>()
            val storesList = commerce.stores()
            if(!storesList.isNullOrEmpty()){
                storesList.forEach {store ->
                    val newStore = handleStoreSchedules(store)
                    commerceStores.add(newStore)
                }
            }
            val offer = Offer(commerce.cashback())
            return@map Commerce(commerce.id(),commerce.name(),commerce.posterImage(), commerce.website()?:"",commerce.facebook()?:"",
                commerce.whatsapp()?:"",commerce.instagram()?:"",commerce.category(),commerceStores,"El descuento no aplica para otros descuentos.",
                "El descuento es válido solo pagando con RediPuntos.",commerce.description(), offer, commerce.cashback(), commerce.isFavorite)
        }
        return commerces

    }

    private fun handleStoreSchedules(store : GetCommercesQuery.Store) : Store{
        val location = Location(store.name(),store.location().lat(), store.location().lon())
        var schedule : Schedule ? = null
        if(store.schedule()!=null){
            val sun = ShoppingHour(1,store.schedule()?.sun()?.open()?:false,store.schedule()?.sun()?.openningHour()?:"",store.schedule()?.sun()?.closingHour()?:"")
            val mon = ShoppingHour(2,store.schedule()?.mon()?.open()?:false,store.schedule()?.mon()?.openningHour()?:"",store.schedule()?.mon()?.closingHour()?:"")
            val tue = ShoppingHour(3,store.schedule()?.tue()?.open()?:false,store.schedule()?.tue()?.openningHour()?:"",store.schedule()?.tue()?.closingHour()?:"")
            val wed = ShoppingHour(4,store.schedule()?.wed()?.open()?:false,store.schedule()?.wed()?.openningHour()?:"",store.schedule()?.wed()?.closingHour()?:"")
            val thu = ShoppingHour(5,store.schedule()?.thu()?.open()?:false,store.schedule()?.thu()?.openningHour()?:"",store.schedule()?.thu()?.closingHour()?:"")
            val fri = ShoppingHour(6,store.schedule()?.fri()?.open()?:false,store.schedule()?.fri()?.openningHour()?:"",store.schedule()?.fri()?.closingHour()?:"")
            val sat = ShoppingHour(7,store.schedule()?.sat()?.open()?:false,store.schedule()?.sat()?.openningHour()?:"",store.schedule()?.sat()?.closingHour()?:"")
            schedule = Schedule(sun, mon, tue, wed, thu, fri, sat)

        }
        return Store(store.id(),store.name(),location,store.phoneNumber(),schedule)
    }

}