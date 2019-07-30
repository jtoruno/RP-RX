package com.zimplifica.domain.entities

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


enum class TransactionType{
    directPayment
}
enum class TransactionStatus{
    success,pending,fail
}

class CardDetail(val cardId: String, val cardNumber: String, val issuer: String): Serializable

class WayToPay(val rediPuntos: Double, val creditCard: CardDetail? = null, val creditCardCharge: Double): Serializable

class GTIItem(val type: String,val description: String, val amount: Double, val companyId: String, val agreementId: String,
              val invoiceId: String, val clientName: String,val serviceId: String,val  paymentType: String,
              val expirationDate: String, val period: String, isPrepaid: Boolean): Serializable

class SitePaymentItem(val type: String, val description: String?, val amount: Double, val vendorId: String, val vendorName: String): Serializable

class TransactionDetail(var type: String , val amount: Double, val vendorId: String, val vendorName: String): Serializable

class Transaction(
    var id: String, val datetime: String, val transactionType: String, val transactionDetail: TransactionDetail, val fee: Double, val tax: Double, val subtotal : Double,
    val total: Double, val rewards: Double, val status: TransactionStatus, val wayToPay: WayToPay, val description: String?): Serializable{
    var date : String
    var time : String
    init {
        val dateNow = Date()
        dateNow.time = datetime.toLong()
        date = SimpleDateFormat("dd/MM/yyyy").format(dateNow)
        time = SimpleDateFormat("HH:mm").format(dateNow)
    }

    constructor() : this("","1564437869927","", TransactionDetail("",0.0,"",""),0.0,0.0,
        0.0,0.0,0.0,TransactionStatus.fail,WayToPay(0.0,null,0.0),null)

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Transaction) return false
        return id == other.id && date == other.date
    }
}

class TransactionsResult(val transactions : List<Transaction>, val nextToken : String?)