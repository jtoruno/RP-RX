package com.zimplifica.awsplatform.useCases

import android.content.Context
import android.util.Log
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.amazonaws.rediPuntosAPI.*
import com.amazonaws.rediPuntosAPI.type.CheckoutPayloadSitePayInput
import com.amazonaws.rediPuntosAPI.type.WayToPayInput
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.zimplifica.awsplatform.AppSync.AppSyncClient
import com.zimplifica.awsplatform.AppSync.CacheOperations
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
                        val way2pay = WayToPay(result.wayToPay().rediPuntos(),cardDetail,0.0,result.wayToPay().creditCardCharge())
                        val transactionDetail = TransactionDetail(TransactionType.directPayment,result.paymentDescription(),result.item().amount(),null,null)
                        transactionDetail.type = TransactionType.directPayment
                        transactionDetail.sitePaymentItem = SitePaymentItem(result.item().type(),result.paymentDescription(),result.item().amount(),result.item().vendorId(),
                            result.item().vendorName())
                        /*
                        when(result.transactionType()){
                            "GTIItem" -> {
                                val gtiItem = result.item().asGTIItem()
                                if(gtiItem!=null){
                                    transactionDetail.type = TransactionType.servicePayment
                                    transactionDetail.gtiItem = GTIItem(result.item().type(),gtiItem.description(),gtiItem.amount(),gtiItem.companyId(),gtiItem.agreementId(),
                                        gtiItem.invoiceId(),gtiItem.clientName(),gtiItem.serviceId(),gtiItem.paymentType(),gtiItem.expirationDate(),gtiItem.period(),
                                        gtiItem.isPrepaid)
                                }
                            }
                            "sitepay" -> {
                                val sitePayItem = result.item().asSitePaymentItem()
                                if(sitePayItem!=null){
                                    transactionDetail.type = TransactionType.directPayment
                                    transactionDetail.sitePaymentItem = SitePaymentItem(result.item().type(),sitePayItem.description(),sitePayItem.amount(),sitePayItem.vendorId(),
                                        sitePayItem.vendorName())
                                }
                            }
                            else -> {
                                val sitePayItem = result.item().asSitePaymentItem()
                                if(sitePayItem!=null){
                                    transactionDetail.type = TransactionType.directPayment
                                    transactionDetail.sitePaymentItem = SitePaymentItem(result.item().type(),sitePayItem.description(),sitePayItem.amount(),sitePayItem.vendorId(),
                                        sitePayItem.vendorName())
                                }
                            }
                        }*/
                        var status : TransactionStatus = when(result.status()){
                            "Successful" -> TransactionStatus.success
                            "pending" -> TransactionStatus.pending
                            "ValitionFailure" -> TransactionStatus.fail
                            else -> TransactionStatus.fail
                        }
                        val transaction = Transaction(result.id(),result.datetime(),result.transactionType(),transactionDetail,
                            result.fee(),result.tax(), result.subtotal(),result.total(),result.rewards(),status,way2pay)

                        //val requestPayment = RequestPayment(true, "")

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
            this.appSyncClient!!.query(query).enqueue(object : GraphQLCall.Callback<GetCheckoutPayloadSitePayQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [checkoutPayloadSitePay] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetCheckoutPayloadSitePayQuery.Data>) {
                    val result = response.data()?.checkoutPayloadSitePay
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
            val input = com.amazonaws.rediPuntosAPI.type.PaymentMethodInput.builder()
                .cardNumber(paymentMethod.cardNumber)
                .cardHolderName(paymentMethod.cardHolderName)
                .expirationDate(paymentMethod.expirationDate)
                .cvv(paymentMethod.cvv)
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

    override fun fetchTransactions(username: String, useCache: Boolean): Observable<Result<TransactionsResult>> {
        val single = Single.create<Result<TransactionsResult>> create@{ single ->
            val query = GetTransactionsByUserQuery.builder()
                //.username(username)
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
                            val wayToPay = WayToPay(trx.wayToPay().rediPuntos(),cardDetail,0.0,trx.wayToPay().creditCardCharge())
                            var transactionDetail = TransactionDetail(TransactionType.directPayment,trx.paymentDescription(),trx.item().amount(),null,null)
                            transactionDetail.type = TransactionType.directPayment
                            transactionDetail.sitePaymentItem = SitePaymentItem(trx.item().type(),trx.paymentDescription(),trx.item().amount(),trx.item().vendorId(),
                                trx.item().vendorName())
                            /*
                            when(trx.item().type()){
                                "GTIItem" -> {
                                    val gtiItem = trx.item().asGTIItem()
                                    if(gtiItem!=null){
                                        transactionDetail.type = TransactionType.servicePayment
                                        transactionDetail.gtiItem = GTIItem(trx.item().type(),gtiItem.description(),gtiItem.amount(),gtiItem.companyId(),gtiItem.agreementId(),
                                            gtiItem.invoiceId(),gtiItem.clientName(),gtiItem.serviceId(),gtiItem.paymentType(),gtiItem.expirationDate(),gtiItem.period(),
                                            gtiItem.isPrepaid)
                                    }
                                }
                                "sitepay" -> {
                                    val sitePayItem = trx.item().asSitePaymentItem()
                                    if(sitePayItem!=null){
                                        transactionDetail.type = TransactionType.directPayment
                                        transactionDetail.sitePaymentItem = SitePaymentItem(trx.item().type(),sitePayItem.description(),sitePayItem.amount(),sitePayItem.vendorId(),
                                            sitePayItem.vendorName())
                                    }
                                }
                                else -> {
                                    val sitePayItem = trx.item().asSitePaymentItem()
                                    if(sitePayItem!=null){
                                        transactionDetail.type = TransactionType.directPayment
                                        transactionDetail.sitePaymentItem = SitePaymentItem(trx.item().type(),sitePayItem.description(),sitePayItem.amount(),sitePayItem.vendorId(),
                                            sitePayItem.vendorName())
                                    }
                                }
                            }*/
                            var status : TransactionStatus = when(trx.status()){
                                "successful" -> TransactionStatus.success
                                "pending" -> TransactionStatus.pending
                                "valitionFailure" -> TransactionStatus.fail
                                else -> TransactionStatus.fail
                            }
                            return@map Transaction(trx.id(), trx.datetime(),trx.transactionType(), transactionDetail,trx.fee(),trx.tax(),trx.subtotal(),trx.total(),trx.rewards(),status, wayToPay)
                        }
                        val transactionsResult = TransactionsResult(transactions)
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

}