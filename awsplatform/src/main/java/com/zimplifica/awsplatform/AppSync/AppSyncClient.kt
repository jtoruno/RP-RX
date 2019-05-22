package com.zimplifica.awsplatform.AppSync

import android.content.Context
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient


object AppSyncClient{
    enum class API{
        rediPuntosAPI
    }
    enum class Source {
        cache, server, unkwnon
    }
    private var clientSync : AWSAppSyncClient ? = null

    private val key = "rediPuntosAPI"

    fun getClient() : AWSAppSyncClient{
        return this.clientSync!!
    }

    fun initClient(context: Context){
        val id = context.resources.getIdentifier("awsconfiguration","raw",context.packageName)

        this.clientSync = AWSAppSyncClient.builder()
            .credentialsProvider(AWSMobileClient.getInstance())
            .awsConfiguration(AWSConfiguration(context,id,key))
            .context(context)
            .build()
    }
}

/*
class AppSyncClient(val context: Context){
    enum class API{
        rediPuntosAPI
    }
    enum class Source {
        cache, server, unkwnon
    }

    var client : AWSAppSyncClient ? = null

    init {
        client = getInstance()
    }

    private fun getInstance() : AWSAppSyncClient{
        if(client == null){
            client = AWSAppSyncClient.builder()
                .credentialsProvider(AWSMobileClient.getInstance())
                .context(context)
                .build()
        }
        return client!!
    }

}
/*
class AppSyncClient(val context: Context){
    private var client : AWSAppSyncClient ?= null
    fun getInstance() : AWSAppSyncClient{
        if(client == null){
            client = AWSAppSyncClient.builder()
                .credentialsProvider(AWSMobileClient.getInstance())
                .context(context)
                .build()
        }
        return client!!
    }
}*/


/*
class AppSyncClient {
    private var client : AWSAppSyncClient ? = null
    enum class API{
        rediPuntosAPI
    }
    @Synchronized
    fun getInstance() : AWSAppSyncClient{
        if(client == null){
            client = AWSAppSyncClient.builder()
                .credentialsProvider(AWSMobileClient.getInstance())
                .build()
        }
        return client!!
    }
*/
    /*
    companion object {
        private lateinit var client : AWSAppSyncClient
        val shared : AWSAppSyncClient
        get() {
            if(client == null){
                client = AWSAppSyncClient.builder()
                    .credentialsProvider(AWSMobileClient.getInstance())
                    .build()
            }
            return client
        }
    }

}*/