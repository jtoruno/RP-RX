package com.zimplifica.domain.entities

import org.json.JSONException
import org.json.JSONObject

class SecurityCode(val pin : String, val verificationCode : String?){
    fun getJson() : String?{
        val jsonObject = JSONObject()
        return try {
            jsonObject.put("pin",pin)
            jsonObject.put("verificationCode",verificationCode)
            jsonObject.toString()
        } catch (e: JSONException){
            e.printStackTrace()
            null
        }
    }
}