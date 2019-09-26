package com.zimplifica.domain.entities

import org.json.JSONException
import org.json.JSONObject

class ChangePasswordModel(val verificationCode : String, val password : String) {
    fun toJson() : String?{
        val jsonObject = JSONObject()
        return try {
            jsonObject.put("verificationCode",verificationCode)
            jsonObject.put("password",password)
            jsonObject.toString()
        }catch (e: JSONException){
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }
    }
}