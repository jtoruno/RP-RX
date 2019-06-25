package com.zimplifica.redipuntos.mocks

import com.zimplifica.domain.entities.*
import com.zimplifica.domain.useCases.UserUseCase
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.runner.Request
import java.lang.Exception

class UserUseCase : UserUseCase{
    override fun disablePaymentMethod(owner: String, cardId: String): Observable<Result<PaymentMethod>> {
        val single = Single.create<Result<PaymentMethod>> create@{ single ->
            println("disable ${owner}")
            if(owner == "1234" && cardId == "1234"){
                val paymentM = PaymentMethod("XXXXXX-XXXXX-XXX", "9944", "10/20","VISA", 10.0,  true)
                single.onSuccess(Result.success(paymentM))
            }
            else{
                val error = Exception("disablePaymentMethod")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun getUserInformation(useCache: Boolean): Observable<Result<UserInformationResult>> {
        val single = Single.create<Result<UserInformationResult>> create@{
            val userInfo = UserInformationResult( "e5a06e84-73f4-4a04-bcbc-a70552a4d92a", "115650044",
                "PEDRO",  "FONSECA ARGUEDAS",  "10/10/1993",
                 "pedro@redi.com",  "+50699443322",
                 true,  true, null,  0.0,  mutableListOf())
            it.onSuccess(Result.success(userInfo))
        }
        return single.toObservable()
    }

    override fun fetchTransactions(username: String, useCache: Boolean): Observable<Result<TransactionsResult>> {
        val single = Single.create<Result<TransactionsResult>> create@{ single ->
            if(username == "1234"){
                val cardDetail = CardDetail("12312412512","2324", "visa")
                val wayToPay = WayToPay(4000.0,cardDetail, 1000.0,3000.0)
                val sitePaymentItem = SitePaymentItem("","Test",2000.0,"231421agewg24-2131-fwawefa-f2332",
                    "Manpuku Sushi")
                val transactionDetail = TransactionDetail(TransactionType.directPayment, "Test",5550.0,null, sitePaymentItem)
                val transactions = mutableListOf<Transaction>()
                transactions.add(Transaction("1234", "11-11-2019", "debit", transactionDetail, 0.0, 50.0,6500.0,6500.0,25.0,TransactionStatus.success, wayToPay))
                transactions.add(Transaction("4321","11-11-2019","debit",transactionDetail, 0.0,50.0,20500.0,20500.0,40.0,TransactionStatus.fail,wayToPay))
                val transactionsResult = TransactionsResult(transactions)
                single.onSuccess(Result.success(transactionsResult))
            } else {
                val error = Exception("")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun updateUserInfo(citizen: CitizenInput): Observable<Result<Citizen>> {
        return Observable.never()
    }

    override fun addPaymentMethod(paymentMethod: PaymentMethodInput): Observable<Result<PaymentMethod>> {
        val single = Single.create<Result<PaymentMethod>> create@{ single ->
            if(paymentMethod.cardNumber == "4539169425022428"){
                val paymentM = PaymentMethod("XXXXXX-XXXXX-XXX","9944","10/20","VISA",10.0,true)
                single.onSuccess(Result.success(paymentM))
            }
            else{
                val error = Exception("addPaymentMethod")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun checkoutPayloadSitePay(
        username: String,
        amount: Float,
        vendorId: String,
        description: String
    ): Observable<Result<PaymentPayload>> {
        val single = Single.create<Result<PaymentPayload>> create@{ single ->
            if (vendorId == "7120-39345-1023841023-123434"){
                val item = Item("","",5555.5)
                val order = Order("3c288f1b-e95f-40a2-8f53-40b61d356156", item, 0.0, 5555.5,5555.5,5555.5,40.0)
                val paymentMethods = mutableListOf<PaymentMethod>()
                paymentMethods.add(PaymentMethod("1234321412341234","1234","","visa",4505.0,false))
                val paymentPayload = PaymentPayload(1000.0,order,paymentMethods)
                single.onSuccess(Result.success(paymentPayload))
            }else{
                val error = Exception("")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun requestPayment(requestPaymentInput: RequestPaymentInput): Observable<Result<Transaction>> {
        val single = Single.create<Result<Transaction>> create@{ single ->
            if(requestPaymentInput.orderId == "3c288f1b-e95f-40a2-8f53-40b61d356156"){
                val cardDetail = CardDetail("12312412512","2324", "visa")
                val wayToPay = WayToPay(4000.0,cardDetail, 1000.0,3000.0)
                val sitePaymentItem = SitePaymentItem("","Test",2000.0,"231421agewg24-2131-fwawefa-f2332",
                    "Manpuku Sushi")
                val transactionDetail = TransactionDetail(TransactionType.directPayment, "Test",5550.0,null, sitePaymentItem)
                val transaction = Transaction("1234", "11-11-2019", "debit", transactionDetail, 0.0, 50.0,6500.0,6500.0,25.0,TransactionStatus.success, wayToPay)
                //val requestPayment = RequestPayment(true,"Success")
                single.onSuccess(Result.success(transaction))
            }else{
                val error = Exception("")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun getVendorInformation(vendorId: String): Observable<Result<Vendor>> {
        val single = Single.create<Result<Vendor>> create@{ single ->
            if(vendorId == "7120-39345-1023841023-123434"){
                val vendor = Vendor("7120-39345-1023841023-123434","Manpuku Sushi","Jaco")
                single.onSuccess(Result.success(vendor))
            }else{
                val error = Exception("")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

}