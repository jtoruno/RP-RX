package com.zimplifica.awsplatform.AppSync

import android.content.Context
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.cache.normalized.CacheKeyResolver









object AppSyncClient{
    enum class AppSyncClientMode {
        PUBLIC,
        PRIVATE
    }

    enum class Source {
        cache, server, unkwnon
    }

    private var CLIENTS: Map<AppSyncClientMode,AWSAppSyncClient> ?= null

    //private var clientSync : AWSAppSyncClient ? = null

    private val publicKey = "publicAPI"
    private val privateKey = "privateAPI"

    /*
    fun getClient() : AWSAppSyncClient{
        return this.clientSync!!
    }*/

    fun getClient(choice : AppSyncClientMode) : AWSAppSyncClient?{
        return this.CLIENTS?.get(choice)
    }

    fun initClients(context: Context){
        val id = context.resources.getIdentifier("awsconfiguration","raw",context.packageName)

        var array = mutableMapOf<AppSyncClientMode,AWSAppSyncClient>()
        /*
        this.clientSync = AWSAppSyncClient.builder()
            .credentialsProvider(AWSMobileClient.getInstance())
            .awsConfiguration(AWSConfiguration(context,id, publicKey))
            .context(context)
            .build()*/

        array[AppSyncClientMode.PUBLIC] = AWSAppSyncClient.builder()
            .context(context)
            .awsConfiguration(AWSConfiguration(context,id, publicKey))
            .useClientDatabasePrefix(true)
            .credentialsProvider(AWSMobileClient.getInstance())
            .build()

        array[AppSyncClient.AppSyncClientMode.PRIVATE] = AWSAppSyncClient.builder()
            .context(context)
            .awsConfiguration(AWSConfiguration(context,id, privateKey))
            .cognitoUserPoolsAuthProvider(CognitoUserPoolsAuthProvider {
                try {
                    return@CognitoUserPoolsAuthProvider AWSMobileClient.getInstance().tokens.idToken.tokenString
                } catch (e: Exception) {
                    Log.e("APPSYNC_ERROR", e.localizedMessage)
                    return@CognitoUserPoolsAuthProvider e.localizedMessage
                }
            })
            .useClientDatabasePrefix(true)
            .resolver(object : CacheKeyResolver() {
                override fun fromFieldRecordSet(field: ResponseField, recordSet: MutableMap<String, Any>): CacheKey {
                    return formatCacheKey(recordSet["id"] as String?)
                }

                override fun fromFieldArguments(field: ResponseField, variables: Operation.Variables): CacheKey {
                    return formatCacheKey(field.resolveArgument("id", variables) as String?)
                }

                private fun formatCacheKey(id: String?): CacheKey {
                    return if (id == null || id.isEmpty()) {
                        CacheKey.NO_KEY
                    } else {
                        CacheKey.from(id)
                    }
                }
            })
            .build()

        this.CLIENTS = array
    }
}