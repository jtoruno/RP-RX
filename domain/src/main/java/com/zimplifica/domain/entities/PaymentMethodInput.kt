package com.zimplifica.domain.entities

import org.json.JSONException
import org.json.JSONObject



class PaymentMethodInput(val cardNumber: String, val cardHolderName: String, val expirationDate: String, val cvv: String){
    fun toJson() : String{
        val jsonObject = JSONObject()
        return try {
            jsonObject.put("cardNumber",cardNumber)
            jsonObject.put("cardHolderName", cardHolderName)
            jsonObject.put("expirationDate", expirationDate)
            jsonObject.put("cvv",cvv)
            jsonObject.toString()
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            ""
        }

    }
}