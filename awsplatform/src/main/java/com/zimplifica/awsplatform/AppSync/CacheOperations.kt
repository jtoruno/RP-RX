package com.zimplifica.awsplatform.AppSync

import android.util.Log
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.amazonaws.rediPuntosAPI.GetUserQuery
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.zimplifica.domain.entities.Citizen
import com.zimplifica.domain.entities.PaymentMethod

class CacheOperations{
    private var appSyncClient = AppSyncClient.getClient(AppSyncClient.AppSyncClientMode.PRIVATE)

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
                            user.email(), user.emailVerified(),user.rewards(),user.paymentMethods())

                        val data = query.wrapData(GetUserQuery.Data(item))

                        appSyncClient?.store?.write(query,data)?.enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [updateCitizen]")
                    }
                }

            })
    }

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
                        val item = GetUserQuery.GetUser(user.__typename(),user.id(),user.firstName(),
                            user.lastName(),user.birthdate(),user.identityNumber(),user.phoneNumber(),user.phoneNumberVerified(),
                            email, user.emailVerified(),user.rewards(),user.paymentMethods())

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
                        val item = GetUserQuery.GetUser(user.__typename(),user.id(),user.firstName(),
                            user.lastName(),user.birthdate(),user.identityNumber(),user.phoneNumber(),user.phoneNumberVerified(),
                            user.email(), isConfirmed,user.rewards(),user.paymentMethods())

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
                            paymentMethod.cardNumberWithMask, paymentMethod.cardExpirationDate, paymentMethod.issuer, paymentMethod.rewards,
                            paymentMethod.automaticRedemption)
                        paymentList.add(paymentObj)
                        val item = GetUserQuery.GetUser(user.__typename(),user.id(),user.firstName(),
                            user.lastName(),user.birthdate(),user.identityNumber(),user.phoneNumber(),user.phoneNumberVerified(),
                            user.email(), user.emailVerified(),user.rewards(),paymentList)

                        val data = query.wrapData(GetUserQuery.Data(item))

                        appSyncClient!!.store.write(query,data).enqueue(null)
                        Log.i("🔵", "Cache updated at [CacheOperations] [addPaymentMethod]")
                    }
                }
            })
    }

}