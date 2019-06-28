package com.zimplifica.domain.entities

class WayToPayInput(val rediPuntos: Double, var creditCardId: String? = null, var creditCardRewards: Double,var creditCardCharge: Double)

class RequestPaymentInput(val username: String, val orderId: String, val wayToPay: WayToPayInput,val paymentDescription: String?)