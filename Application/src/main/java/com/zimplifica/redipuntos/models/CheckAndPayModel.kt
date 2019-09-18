package com.zimplifica.redipuntos.models

import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.domain.entities.Vendor
import java.io.Serializable
import kotlin.math.floor

class CheckAndPayModel(val orderId: String,val subtotal: Double,val fee: Double,val tax: Double,val total: Double,val rediPuntos: Double,val cashback: Int,val taxes: Int,var paymentMethods: List<PaymentMethod>,val vendor: Vendor) : Serializable{
    var applyRediPuntos : Boolean
    var selectedPaymentMethod : PaymentMethod ? = null
    var description : String?
    init {
        applyRediPuntos =  true
        if (paymentMethods.isNotEmpty()){
            selectedPaymentMethod = paymentMethods.first()
        }
        description = null
    }

    fun rediPuntosToApply() : Double{
        return if (applyRediPuntos){
            if (total - rediPuntos < 0){
                total
            }else{
                rediPuntos
            }
        }else{
            0.0
        }
    }

    fun chargeToApply() : Double{
        return total - rediPuntosToApply()
    }

    fun rewardsToGet() : Double{
        val t = 1.0 + (taxes/100.0)
        val amountWithoutTaxes = floor(total/t)
        val amountWithoutRps = amountWithoutTaxes - rediPuntosToApply()
        val c = cashback/100.0
        return floor(amountWithoutRps*c)
    }

}