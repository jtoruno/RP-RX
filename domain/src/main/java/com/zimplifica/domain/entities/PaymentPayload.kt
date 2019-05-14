package com.zimplifica.domain.entities

class PaymentMethod(val cardId: String,val cardNumberWithMask: String,
                     val cardExpirationDate: String, val issuer: String,
                     val rewards: Double, val automaticRedemption: Boolean) {
}

class Item (val type: String, val description: String, val amount: Double)

class Order(val pid: String, val item: Item,val fee: Double,val total: Double)

class PaymentPayload(val rediPuntos: Double, val order: Order, val paymentMethods: List<PaymentMethod>)