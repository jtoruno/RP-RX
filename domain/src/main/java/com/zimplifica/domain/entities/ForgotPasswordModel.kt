package com.zimplifica.domain.entities

import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordModel(val verificationCode: String, val password: String, val phoneNumber: String) {
    fun getJson() : String?{
        val jsonObject = JSONObject()
        return try {
            jsonObject.put("verificationCode",verificationCode)
            jsonObject.put("password",password)
            jsonObject.put("phoneNumber",phoneNumber)
            jsonObject.toString()
        } catch (e: JSONException){
            e.printStackTrace()
            null
        }
    }
}