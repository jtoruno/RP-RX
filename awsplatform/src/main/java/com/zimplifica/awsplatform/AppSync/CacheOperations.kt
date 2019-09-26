package com.zimplifica.awsplatform.AppSync

import android.util.Log
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.amazonaws.rediPuntosAPI.GetCommercesQuery
import com.amazonaws.rediPuntosAPI.GetNotificationsQuery
import com.amazonaws.rediPuntosAPI.GetTransactionsByUserQuery
import com.amazonaws.rediPuntosAPI.GetUserQuery
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.zimplifica.domain.entities.Citizen
import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.domain.entities.Transaction
import com.zimplifica.domain.entities.VerificationStatus

class CacheOperations{
    private var appSyncClient = AppSyncClient.getClient(AppSyncClient.AppSyncClientMode.PRIVATE)

    /*
    fun updateCitizen(citizen: Citizen){
        val query = GetUserQuery.builder().build()
        appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.CACHE_ONLY)
            .enqueue(object: GraphQLCall.Callback<GetUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, CacheOperations,Citizen Error:", e)
                }

                override fun onResponse(response: Response<GetUserQuery.Data>) {
                    val user = response.data()?.user
                    if(user!= null){
                        val item = GetUserQuery.GetUser(user.__typename(),user.id(),citizen.firstName,
                            citizen.lastName,citizen.getBirthDateAsString(),citizen.identityNumber,user.phoneNumber(),user.phoneNumberVerified(),
                            user.email(), user.emailVerified(),user.rewards(),user.paymentMethods(), user.status(),user.gender(),user.address())

                        val data = query.wrapData(GetUserQuery.Data(item))

                        appSyncClient?.store?.write(query,data)?.enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [updateCitizen]")
                    }
                }

            })
    }*/

    fun updateEmail(email : String){
        val query = GetUserQuery.builder().build()
        appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.CACHE_ONLY)
            .enqueue(object: GraphQLCall.Callback<GetUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, CacheOperations,updateEmail Error:", e)
                }

                override fun onResponse(response: Response<GetUserQuery.Data>) {
                    val user = response.data()?.user
                    if(user!= null){
                        val item = GetUserQuery.GetUser(user.__typename(),user.id(),user.nickname(),user.identityNumber(),user.phoneNumber(),user.phoneNumberVerified(),
                            email, user.emailVerified(),user.rewards(),user.paymentMethods(),user.hasSecurityCode())

                        val data = query.wrapData(GetUserQuery.Data(item))

                        appSyncClient!!.store.write(query,data).enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [updateEmail]")
                    }
                }

            })
    }

    fun updateEmailStatus(isConfirmed : Boolean){
        val query = GetUserQuery.builder().build()
        appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.CACHE_ONLY)
            .enqueue(object: GraphQLCall.Callback<GetUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, CacheOperations,updateEmail Error:", e)
                }

                override fun onResponse(response: Response<GetUserQuery.Data>) {
                    val user = response.data()?.user
                    if(user!= null){
                        val item = GetUserQuery.GetUser(user.__typename(),user.id(),user.nickname(),user.identityNumber(),user.phoneNumber(),user.phoneNumberVerified(),
                            user.email(), isConfirmed,user.rewards(),user.paymentMethods(),user.hasSecurityCode())
                        val data = query.wrapData(GetUserQuery.Data(item))

                        appSyncClient!!.store.write(query,data).enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [updateEmailStatus]")
                    }
                }
            })
    }

    //Probar con 0 tarrjetas, por el typename
    fun addPaymentMethod(paymentMethod: PaymentMethod){
        val query = GetUserQuery.builder().build()
        appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.CACHE_ONLY)
            .enqueue(object: GraphQLCall.Callback<GetUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, CacheOperations,updateEmail Error:", e)
                }

                override fun onResponse(response: Response<GetUserQuery.Data>) {
                    val user = response.data()?.user
                    if(user!= null){
                        val paymentList = user.paymentMethods().toMutableList()
                        val paymentObj = GetUserQuery.PaymentMethod("__PaymentMethod",paymentMethod.cardId,
                            paymentMethod.cardNumberWithMask, paymentMethod.cardExpirationDate, paymentMethod.issuer)
                        paymentList.add(paymentObj)
                        val item = GetUserQuery.GetUser(user.__typename(),user.id(),user.nickname(),user.identityNumber(),user.phoneNumber(),user.phoneNumberVerified(),
                            user.email(), user.emailVerified(),user.rewards(),paymentList,user.hasSecurityCode())

                        val data = query.wrapData(GetUserQuery.Data(item))

                        appSyncClient!!.store.write(query,data).enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [addPaymentMethod]")
                    }
                }
            })
    }

    fun updateTransactions(withExistingTransaction : Transaction){

        val query = GetTransactionsByUserQuery.builder().build()
        appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.CACHE_ONLY)
            .enqueue(object : GraphQLCall.Callback<GetTransactionsByUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, CacheOperations,updateTransactions Error:", e)
                }

                override fun onResponse(response: Response<GetTransactionsByUserQuery.Data>) {
                    val result = response.data()?.transactionsByUser
                    if (result!=null){
                        //var trans : GetTransactionsByUserQuery.Item
                        val items = result.items().toMutableList()
                        /*
                        val iterator = items.iterator()

                        while (iterator.hasNext()){
                            val oldValue = iterator.next()
                            if(oldValue.id() == withExistingTransaction.orderId){
                                trans = GetTransactionsByUserQuery.Item(oldValue.__typename(),oldValue.id(),oldValue.datetime(),oldValue.transactionType(),oldValue.item(),oldValue.fee(),oldValue.tax(),oldValue.subtotal(),oldValue.total(),
                                    oldValue.rewards(),withExistingTransaction.status.name,oldValue.wayToPay(),oldValue.paymentDescription())
                            }
                        }*/
                        items.map { oldValue -> if(oldValue.id() == withExistingTransaction.id){
                            return@map GetTransactionsByUserQuery.Item(oldValue.__typename(),oldValue.id(),oldValue.datetime(),oldValue.transactionType(),oldValue.item(),oldValue.fee(),oldValue.tax(),oldValue.subtotal(),oldValue.total(),
                                oldValue.rewards(),withExistingTransaction.status.name,oldValue.wayToPay(),oldValue.paymentDescription())
                        }else{
                            return@map oldValue
                        } }

                        val data = query.wrapData(GetTransactionsByUserQuery.Data(GetTransactionsByUserQuery.GetTransactionsByUser("PaginatedTransactions",items,null)))
                        appSyncClient!!.store.write(query,data).enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [updateTransactions]")
                    }
                } })
    }
    fun updateNewTransactions(newTransaction: Transaction){
        val query = GetTransactionsByUserQuery.builder().build()
        appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.CACHE_ONLY)
            .enqueue(object : GraphQLCall.Callback<GetTransactionsByUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, CacheOperations,updateTransactions Error:", e)
                }

                override fun onResponse(response: Response<GetTransactionsByUserQuery.Data>) {
                    val result = response.data()?.transactionsByUser
                    if (result!=null){
                        val items = result.items().toMutableList()
                        val item = GetTransactionsByUserQuery.Item1("",newTransaction.transactionType,newTransaction.transactionDetail.amount,newTransaction.transactionDetail.vendorId,newTransaction.transactionDetail.vendorName)
                        val card = GetTransactionsByUserQuery.CreditCard("",newTransaction.wayToPay.creditCard!!.cardId,newTransaction.wayToPay.creditCard!!.cardNumber,newTransaction.wayToPay.creditCard!!.issuer)
                        val wtp = GetTransactionsByUserQuery.WayToPay("",newTransaction.wayToPay.rediPuntos,card,newTransaction.wayToPay.creditCardCharge)

                        val transaction = GetTransactionsByUserQuery.Item("",newTransaction.id,newTransaction.datetime,newTransaction.transactionType,item,newTransaction.fee,
                            newTransaction.tax,newTransaction.subtotal,newTransaction.total,newTransaction.rewards,newTransaction.status.name,wtp,newTransaction.description)
                        items.add(0,transaction)
                        Log.i("🔵", "Cache updated at [CacheOperations] [updateTransactions]")
                        val data = query.wrapData(GetTransactionsByUserQuery.Data(GetTransactionsByUserQuery.GetTransactionsByUser("PaginatedTransactions",items,null)))
                        appSyncClient!!.store.write(query,data).enqueue(null)
                    }
                }

            })
    }

    fun updateNotificationStatus(id: String){
        val query = GetNotificationsQuery.builder().build()
        appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.CACHE_ONLY)
            .enqueue(object: GraphQLCall.Callback<GetNotificationsQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, CacheOperations,updateNotificationStatus Error:", e)                }

                override fun onResponse(response: Response<GetNotificationsQuery.Data>) {
                    val result = response.data()?.notifications
                    if (result!=null){
                        val notifications = result.items()
                        notifications.map { oldValue -> if(oldValue.id() == id){
                            return@map GetNotificationsQuery.Item(oldValue.__typename(),oldValue.id(),oldValue.origin(),oldValue.createdAt(),oldValue.user(),oldValue.type(),
                                oldValue.title(),oldValue.message(),oldValue.data(),oldValue.actionable(),true,oldValue.hidden())
                        }else{
                            return@map oldValue
                        }}
                        val data = query.wrapData(GetNotificationsQuery.Data(GetNotificationsQuery.GetNotifications(result.__typename(),result.nextToken(),notifications)))
                        appSyncClient!!.store.write(query,data).enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [updateNotificationStatus]")
                    }
                }
            })
    }

    fun updateCommerce(favorite: Boolean, merchantId: String) {
        val query = GetCommercesQuery.builder().build()
        appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.CACHE_ONLY)
            .enqueue(object: GraphQLCall.Callback<GetCommercesQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, CacheOperations,updateCommerce Error:", e)                 }

                override fun onResponse(response: Response<GetCommercesQuery.Data>) {
                    val result = response.data()?.commerces
                    if (result!=null){
                        val commerces = result.items()
                        commerces.map { if (it.id() == merchantId){
                            return@map GetCommercesQuery.Item(it.__typename(),it.id(),it.name(),it.description(),it.posterImage(),it.website(),it.facebook(),it.whatsapp(),it.instagram(),it.tags(),it.category(),it.stores(),
                                it.cashback(),favorite)
                        }else{
                            return@map it
                        } }
                        val data = query.wrapData(GetCommercesQuery.Data(GetCommercesQuery.GetCommerces(result.__typename(),commerces,result.total())))
                        appSyncClient!!.store.write(query,data).enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [updateCommerce]")
                    }
                }
            })

    }

    fun updateUserHasSecurityCode(success: Boolean) {
        val query = GetUserQuery.builder().build()
        appSyncClient!!.query(query).responseFetcher(AppSyncResponseFetchers.CACHE_ONLY)
            .enqueue(object: GraphQLCall.Callback<GetUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, CacheOperations,updateEmail Error:", e)
                }

                override fun onResponse(response: Response<GetUserQuery.Data>) {
                    val user = response.data()?.user
                    if(user!= null){
                        val item = GetUserQuery.GetUser(user.__typename(),user.id(),user.nickname(),user.identityNumber(),user.phoneNumber(),user.phoneNumberVerified(),
                            user.email(), user.emailVerified(),user.rewards(),user.paymentMethods(),success)

                        val data = query.wrapData(GetUserQuery.Data(item))

                        appSyncClient!!.store.write(query,data).enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [updateUserHasSecurityCode]")
                    }
                }

            })
    }

}