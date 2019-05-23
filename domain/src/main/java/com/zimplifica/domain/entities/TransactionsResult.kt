package com.zimplifica.domain.entities


enum class TransactionType{
    servicePayment,directPayment
}
enum class TransactionStatus{
    success,pending,fail
}

class CardDetail(val cardId: String, val cardNumber: String, val issuer: String)

class WayToPay(val rediPuntos: Double, val creditCard: CardDetail? = null, val creditCardRewards: Double, val creditCardCharge: Double)

class GTIItem(val type: String,val description: String, val amount: Double, val companyId: String, val agreementId: String,
              val invoiceId: String, val clientName: String,val serviceId: String,val  paymentType: String,
              val expirationDate: String, val period: String, isPrepaid: Boolean)

class SitePaymentItem(val type: String, val description: String, val amount: Double, val vendorId: String, val vendorName: String)

class TransactionDetail(var type: TransactionType, val description: String, val amount: Double, var gtiItem: GTIItem?, var sitePaymentItem: SitePaymentItem?)

class Transaction(val orderId: String, val datetime: String, val transactionType: String, val transactionDetail: TransactionDetail, val fee: Double,
                  val total: Double, val status: TransactionStatus, val wayToPay: WayToPay)

class TransactionsResult(val transactions : List<Transaction>)