package com.zimplifica.awsplatform.AppSync

import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
//import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
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