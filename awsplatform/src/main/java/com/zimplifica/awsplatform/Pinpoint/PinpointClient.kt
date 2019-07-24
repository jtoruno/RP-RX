package com.zimplifica.awsplatform.Pinpoint

import android.content.Context
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager

object PinpointClient {
    private var client : PinpointManager ? = null

    fun getClient() : PinpointManager? {
        return this.client
    }

    fun initClient(context: Context){
        val awsConfig = AWSConfiguration(context)
        val pinpointConfig = PinpointConfiguration(context,AWSMobileClient.getInstance(),awsConfig)
        this.client = PinpointManager(pinpointConfig)
    }

    fun setTokenDevice(token : String) : String {
        return if(client!=null){
            client?.notificationClient?.registerDeviceToken(token)
            token
        }else{
            Log.e("\uD83D\uDD34","Error, set token Pinpoint")
            ""
        }
    }
}