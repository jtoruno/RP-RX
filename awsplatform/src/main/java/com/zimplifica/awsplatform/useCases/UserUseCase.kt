package com.zimplifica.awsplatform.useCases

import android.content.Context
import android.util.Log
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.amazonaws.rediPuntosAPI.*
import com.amazonaws.rediPuntosAPI.type.CheckoutPayloadSitePayInput
import com.amazonaws.rediPuntosAPI.type.CommercesInput
import com.amazonaws.rediPuntosAPI.type.WayToPayInput
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.zimplifica.awsplatform.AppSync.AppSyncClient
import com.zimplifica.awsplatform.AppSync.CacheOperations
import com.zimplifica.awsplatform.Pinpoint.PinpointClient
import com.zimplifica.awsplatform.Utils.PlatformUtils
import com.zimplifica.domain.entities.*
import com.zimplifica.domain.useCases.UserUseCase
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class UserUseCase : UserUseCase {

    private val appSyncClient = AppSyncClient.getClient(AppSyncClient.AppSyncClientMode.PRIVATE)
    private val cacheOperations = CacheOperations()

    override fun disablePaymentMethod(owner: String, cardId: String): Observable<Result<PaymentMethod>> {
        val single = Single.create<Result<PaymentMethod>> create@{ single ->
            val mutation = DisablePaymentMethodMutation.builder()
                .cardId(cardId)
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
                            result.issuer(),result.rewards(),result.automaticRedemption())
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
                //.creditCardRewards(requestPaymentInput.wayToPay.creditCardRewards)
                .creditCardCharge(requestPaymentInput.wayToPay.creditCardCharge)
                .build()
            val input = com.amazonaws.rediPuntosAPI.type.RequestPaymentInput.builder()
                .wayToPay(wayToPayInput)
                .paymentDescription(requestPaymentInput.paymentDescription)
                .orderId(requestPaymentInput.orderId)
                //.username(requestPaymentInput.username)
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
        username: String,
        amount: Float,
        vendorId: String,
        description: String
    ): Observable<Result<PaymentPayload>> {
        val single = Single.create<Result<PaymentPayload>> create@{ single ->
            val input = CheckoutPayloadSitePayInput.builder()
                //.username(username)
                .amount(amount.toDouble())
                .vendorId(vendorId)
                //.description(description)
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
                        val order = Order(result.order().id(), item,result.order().fee(),result.order().tax(),result.order().subtotal(),result.order().total(),result.order().rewards())

                        val rediPuntos = result.paymentOptions().rediPuntos()
                        val paymentMethods = result.paymentOptions().paymentMethods().map { element ->
                            return@map PaymentMethod(element.id(),element.cardNumber(),element.expirationDate(),element.issuer(),
                                element.rewards(),element.automaticRedemption())
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
                .paymentMethod(input)
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
                            result.issuer(),result.rewards(),result.automaticRedemption())
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
            val query = GetTransactionsByUserQuery.builder()
                //.username(username)
                .limit(limit)
                .nextToken(nextToken)
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
                    Log.e("UserUseCase",result.toString())
                    if(result!=null){
                        val items = result.items()
                        val transactions : List<Transaction> = items.map { trx ->
                            var cardDetail : CardDetail ? = null
                            val card = trx.wayToPay().creditCard()
                            if(card != null){
                                cardDetail = CardDetail(card.id(),card.cardNumber(),card.issuer())
                            }
                            /*
                            val cardDetail = CardDetail(trx.wayToPay().creditCard()?.cardId()?: "",trx.wayToPay().creditCard()?.cardNumber()?:"",
                                trx.wayToPay().creditCard()?.issuer()?: "")*/
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
                            return@map PaymentMethod(p.id(),p.cardNumber(),p.expirationDate(),p.issuer(),p.rewards(),p.automaticRedemption())
                        }

                        val userObj = UserInformationResult(user.id(),user.identityNumber(),user.firstName(),user.lastName(),
                            user.birthdate(),user.email(),user.phoneNumber(),user.phoneNumberVerified(),user.emailVerified(),null,user.rewards(),
                            paymentMethods)
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

    override fun updateUserInfo(citizen: CitizenInput): Observable<Result<Citizen>> {
        val single = Single.create<Result<Citizen>> create@{ single ->
            val mutation = UpdatePersonalInfoMutation.builder()
                //.username(citizen.citizenId)
                .firstName(citizen.firstName)
                .lastName(citizen.lastName)
                .birthdate(citizen.birthDate)
                .identityNumber(citizen.citizenId)
                .build()
            this.appSyncClient!!.mutate(mutation).enqueue(object: GraphQLCall.Callback<UpdatePersonalInfoMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [UpdateUserInfo] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<UpdatePersonalInfoMutation.Data>) {
                    val result = response.data()?.updatePersonalInfo()
                    if(result!=null){
                        val citizen = Citizen(result.lastName(),result.firstName(), Date(result.birthdate()) ,result.identityNumber())
                        cacheOperations.updateCitizen(citizen)
                        single.onSuccess(Result.success(citizen))
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
            val query = GetVendorQuery.builder().id(vendorId).build()
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
            val query = GetTransactionByIdQuery.builder().id(id).build()
            this.appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY).enqueue(object:GraphQLCall.Callback<GetTransactionByIdQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [getTransactionById] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetTransactionByIdQuery.Data>) {
                    val result = response.data()?.transactionById
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
                            "ValitionFailure" -> TransactionStatus.fail
                            else -> TransactionStatus.fail
                        }
                        val transaction = Transaction(result.id(),result.datetime(),result.transactionType(),transactionDetail,
                            result.fee(),result.tax(), result.subtotal(),result.total(),result.rewards(),status,way2pay,result.paymentDescription())

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
            val input = CommercesInput.builder().limit(limit).skip(skip) .categoryId(categoryId).textSearch(textSearch).build()
            val query = GetCommercesQuery.builder().input(input).build()
            this.appSyncClient!!.query(query).enqueue(object:GraphQLCall.Callback<GetCommercesQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [getCommerces] Error.",e)
                    single.onSuccess(Result.failure(e))                }

                override fun onResponse(response: Response<GetCommercesQuery.Data>) {
                    val data = response.data()?.commerces
                    if(data!=null){
                        val commerces = handleTransformCommercesInfo(data.items())
                        val commercesResult = CommercesResult(commerces)
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
            val query = GetCategoriesQuery.builder().limit(null).nextToken(null).build()
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

    private fun handleTransformCommercesInfo(data : MutableList<GetCommercesQuery.Item>) : List<Commerce>{
        var commerces : List<Commerce>
        commerces = data.map { commerce ->

            //Stores
            var commerceStores = mutableMapOf<String,Store>()
            val storesList = commerce.stores()
            if (!storesList.isNullOrEmpty()){
                storesList.forEach { store ->
                    val newStore = this.handleStoreSchedules(store)
                    commerceStores[store.id()] = newStore
                }
            }

            //Promotions
            var promotions = mutableListOf<Promotion>()
            val commercePromotions =  commerce.promotions()
            if(!commercePromotions.isNullOrEmpty()){
                promotions = commercePromotions.map { promotion ->
                    var stores = mutableListOf<Store>()
                    promotion.stores()?.forEach { storeIdentification ->
                        if (commerceStores[storeIdentification] != null){
                            stores.add(commerceStores[storeIdentification]!!)
                        }
                    }

                    val offerDiscount = promotion.asOffer()?.discount()
                    if (offerDiscount!=null){
                        val offer = Offer(offerDiscount)
                        return@map Promotion(promotion.id(),promotion.promotionType(),promotion.title(),promotion.description(),promotion.promotionImage(),commerce.name(),promotion.validFrom(),promotion.validTo(),promotion.restrictions(),
                            promotion.waysToUse(),stores,null,offer,commerce.website()?:"",commerce.facebook()?:"",commerce.whatsapp()?:"",commerce.instagram()?:"")
                    }else{
                        val coupon = Coupon(promotion.asCoupon()?.beforeDiscount()?:0.0,promotion.asCoupon()?.afterDiscount()?:0.0)
                        return@map Promotion(promotion.id(),promotion.promotionType(),promotion.title(),promotion.description(),promotion.promotionImage(),commerce.name(),promotion.validFrom(),promotion.validTo(),promotion.restrictions(),promotion.waysToUse(),stores,coupon,
                            null,commerce.website()?:"",commerce.facebook()?:"",commerce.whatsapp()?:"",commerce.instagram()?:"")
                    }
                }.toMutableList()
            }
            return@map Commerce(commerce.id(),commerce.name(),commerce.posterImage(),promotions,commerce.website()?:"",commerce.facebook()?:"",commerce.whatsapp()?:"",commerce.instagram()?:"",commerce.category(),
                mutableListOf())
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