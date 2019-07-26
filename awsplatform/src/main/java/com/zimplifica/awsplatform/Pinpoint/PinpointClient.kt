package com.zimplifica.awsplatform.Pinpoint

import android.content.Context
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager
import com.amazonaws.mobileconnectors.pinpoint.targeting.endpointProfile.EndpointProfile
import com.amazonaws.mobileconnectors.pinpoint.targeting.endpointProfile.EndpointProfileUser
import java.lang.Exception

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

    fun setTokenDevice(token : String, userId : String) : String {
        Log.e("setTokenDevice", "Here")
        return if(client!=null){
            client?.notificationClient?.addDeviceTokenRegisteredHandler {
                Log.e("Device token handler ", it)
            }
            client?.notificationClient?.registerDeviceToken(token)
            val targetingClient = client?.targetingClient
            val endpointProfile = targetingClient?.currentEndpoint()

            val endpointProfileUser = EndpointProfileUser()
            endpointProfileUser.userId = userId
            endpointProfile?.user = endpointProfileUser
            targetingClient?.updateEndpointProfile(endpointProfile)
            Log.d("TokenDevice","Assigned user ID " + endpointProfileUser.userId +
                    " to endpoint " + endpointProfile?.endpointId)


            val currentEndPoint = this.client?.targetingClient?.currentEndpoint()
            Log.e("Profile", currentEndPoint.toString())
            token
        }else{
            Log.e("\uD83D\uDD34","Error, set token Pinpoint")
            ""
        }
    }
}