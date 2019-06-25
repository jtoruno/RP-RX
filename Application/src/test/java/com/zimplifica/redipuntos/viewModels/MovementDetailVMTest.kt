package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class MovementDetailVMTest : RPTestCase() {
    lateinit var vm : MovementDetailVM.ViewModel
    val paymentInfoButtonAction = TestObserver<WayToPay>()
    val transactionAction = TestObserver<Transaction>()


    val cardDetail = CardDetail("12312412512","2324", "visa")
    val wayToPay = WayToPay(4000.0,cardDetail, 1000.0,3000.0)
    val sitePaymentItem = SitePaymentItem("","Test",2000.0,"231421agewg24-2131-fwawefa-f2332",
        "Manpuku Sushi")
    val transactionDetail = TransactionDetail(TransactionType.directPayment, "Test",5550.0,null, sitePaymentItem)
    val transaction = Transaction("1234", "11-11-2019", "debit", transactionDetail, 0.0, 50.0,6500.0,6500.0,25.0,
        TransactionStatus.success, wayToPay)

    private fun setUpEnvironment(environment: Environment){
        vm = MovementDetailVM.ViewModel(environment)
        vm.outputs.paymentInfoButtonAction().subscribe(this.paymentInfoButtonAction)
        vm.intent(Intent().putExtra("transaction",transaction))
        vm.outputs.transactionAction().subscribe(this.transactionAction)
    }

    @Test
    fun testPaymentInfoButtonAction(){
        setUpEnvironment(environment()!!)
        vm.inputs.paymentInfoButtonPressed()
        paymentInfoButtonAction.assertValue(wayToPay)

    }

    @Test
    fun testTransactionAction(){
        setUpEnvironment(environment()!!)
        transactionAction.assertValue(transaction)
    }

}
