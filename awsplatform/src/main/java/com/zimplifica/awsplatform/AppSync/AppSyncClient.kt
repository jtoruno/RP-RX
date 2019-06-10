package com.zimplifica.awsplatform.AppSync

import android.content.Context
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider







object AppSyncClient{
    enum class AppSyncClientMode {
        PUBLIC,
        PRIVATE
    }

    enum class Source {
        cache, server, unkwnon
    }

    private var CLIENTS: Map<AppSyncClientMode,AWSAppSyncClient> ?= null

    private var clientSync : AWSAppSyncClient ? = null

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

        this.clientSync = AWSAppSyncClient.builder()
            .credentialsProvider(AWSMobileClient.getInstance())
            .awsConfiguration(AWSConfiguration(context,id, publicKey))
            .context(context)
            .build()

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
            .build()

        this.CLIENTS = array
    }
}