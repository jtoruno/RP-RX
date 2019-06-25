package com.zimplifica.domain.entities

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


enum class TransactionType{
    servicePayment,directPayment
}
enum class TransactionStatus{
    success,pending,fail
}

class CardDetail(val cardId: String, val cardNumber: String, val issuer: String): Serializable

class WayToPay(val rediPuntos: Double, val creditCard: CardDetail? = null, val creditCardRewards: Double, val creditCardCharge: Double): Serializable

class GTIItem(val type: String,val description: String, val amount: Double, val companyId: String, val agreementId: String,
              val invoiceId: String, val clientName: String,val serviceId: String,val  paymentType: String,
              val expirationDate: String, val period: String, isPrepaid: Boolean): Serializable

class SitePaymentItem(val type: String, val description: String, val amount: Double, val vendorId: String, val vendorName: String): Serializable

class TransactionDetail(var type: TransactionType, val description: String, val amount: Double, var gtiItem: GTIItem?, var sitePaymentItem: SitePaymentItem?): Serializable

class Transaction(val orderId: String, val datetime: String, val transactionType: String, val transactionDetail: TransactionDetail, val fee: Double, val tax: Double, val subtotal : Double,
                  val total: Double, val rewards: Double, val status: TransactionStatus, val wayToPay: WayToPay): Serializable{
    var date : String
    init {
        val dateNow = Date()
        dateNow.time = datetime.toLong()
        date = SimpleDateFormat("dd-MM-yyyy").format(dateNow)
    }
}

class TransactionsResult(val transactions : List<Transaction>)