package com.zimplifica.awsplatform.AppSync

import android.content.Context
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient


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