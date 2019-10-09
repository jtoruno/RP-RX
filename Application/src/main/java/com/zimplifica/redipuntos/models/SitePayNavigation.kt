package com.zimplifica.redipuntos.models

import android.content.Context
import android.content.Intent
import com.zimplifica.domain.entities.PinRequestMode
import com.zimplifica.redipuntos.ui.activities.CreatePinActivity
import com.zimplifica.redipuntos.ui.activities.SPScanQRActivity
import com.zimplifica.redipuntos.ui.activities.UpdatePinActivity
import com.zimplifica.redipuntos.ui.activities.VerifyPinActivity

class SitePayNavigation private constructor(private val context: Context){
    companion object : SingletonHolder<SitePayNavigation,Context>(::SitePayNavigation)
    private var amount = 0F

    fun start(amount : Float){
        this.amount = amount
    }

    fun startNav(securityCode : Boolean){
        if(securityCode){
            val intent = Intent(context, SPScanQRActivity::class.java)
            intent.putExtra("amount", amount)
            startActivity(intent)
        }else{
            val intent = Intent(context, CreatePinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startActivity(intent : Intent){
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}