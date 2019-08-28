package com.zimplifica.domain.entities

import java.io.Serializable

class PaymentMethod(val cardId: String,val cardNumberWithMask: String,
                     val cardExpirationDate: String, val issuer: String,
                     val rewards: Double, val automaticRedemption: Boolean) : Serializable

class Item (val type: String, /*val description: String,*/ val amount: Double) : Serializable

class Order(val pid: String, val item: Item,val fee: Double,val tax : Double, val subtotal : Double,val total: Double, val cashback: Int,val taxes: Int) : Serializable

class PaymentPayload(val rediPuntos: Double, val order: Order, val paymentMethods: List<PaymentMethod>) : Serializable