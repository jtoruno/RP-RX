package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.mocks.creditCardMock
import com.zimplifica.redipuntos.models.CheckAndPayModel
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import com.zimplifica.redipuntos.ui.data.contactEmail
import io.reactivex.observers.TestObserver
import org.junit.Test

class PaymentSelectionVMTest : RPTestCase() {
    lateinit var vm : PaymentSelectionVM.ViewModel
    private val paymentMethodChangedAction = TestObserver<PaymentMethod>()
    private val showError = TestObserver<String>()
    private val finishPaymentProcessingAction = TestObserver<Transaction>()
    private val nextButtonLoadingIndicator = TestObserver<Boolean>()
    private val applyRewards =  TestObserver<Boolean>()
    private val checkAndPayModelAction = TestObserver<CheckAndPayModel>()
    private val reloadPaymentMethodsAction = TestObserver<CheckAndPayModel?>()
    private val addPaymentMethodAction = TestObserver<Unit>()

    private fun setUpEnvironment(environment: Environment){
        val vendor = Vendor( "7120-39345-1023841023-123434",  "Manpuku Sushi",  "Jaco")
        val model = CheckAndPayModel( "3c288f1b-e95f-40a2-8f53-40b61d356156",  1000.0,  100.0,  120.0,  2220.0, 0.0,  10,  13,  creditCardMock,  vendor)

        vm = PaymentSelectionVM.ViewModel(environment)
        vm.intent(Intent().putExtra("CheckAndPayModel",model))
        vm.outputs.addPaymentMethodAction().subscribe(addPaymentMethodAction)
        vm.outputs.paymentMethodChangedAction().subscribe(paymentMethodChangedAction)
        vm.outputs.showError().subscribe(showError)
        vm.outputs.finishPaymentProcessingAction().subscribe(finishPaymentProcessingAction)
        vm.outputs.nextButtonLoadingIndicator().subscribe(nextButtonLoadingIndicator)
        vm.outputs.applyRewards().subscribe(applyRewards)
        vm.outputs.checkAndPayModelAction().subscribe(checkAndPayModelAction)
        vm.outputs.reloadPaymentMethodsAction().subscribe(reloadPaymentMethodsAction)
    }

    @Test
    fun testPaymentMethodChangedAction(){
        val creditCard = PaymentMethod("123412312412312",  "1234",  "11/22",  "VISA")
        setUpEnvironment(environment()!!)
        vm.inputs.paymentMethodChanged(creditCard)
        paymentMethodChangedAction.assertValue(creditCard)
    }

    @Test
    fun testApplyRewards(){
        setUpEnvironment(environment()!!)
        vm.inputs.applyRewardsRowPressed(true)
        applyRewards.assertValue(true)
    }

    @Test
    fun testCheckAndPayModelAction(){
        val vendor = Vendor( "7120-39345-1023841023-123434",  "Manpuku Sushi",  "Jaco")
        val model = CheckAndPayModel( "3c288f1b-e95f-40a2-8f53-40b61d356156",  1000.0,  100.0,  120.0,  2220.0, 0.0,  10,  13,  creditCardMock,  vendor)
        setUpEnvironment(environment()!!)
        checkAndPayModelAction.assertValue(model)
    }

    @Test
    fun testFinishPaymentProcessingAction(){
        setUpEnvironment(environment()!!)
        val wayToPay = WayToPay( 0.0,  null,2113.0)
        val  transactionDetail = TransactionDetail( "debit",  2113.0,  "1234532",  "Zimplifica")
        val transaction = Transaction( "123451234",  "1235123612",  "debit", transactionDetail,  0.0,   0.0,  0.0,  2113.0,  0.0,  TransactionStatus.success,  wayToPay,  "")
        vm.inputs.nextButtonPressed()
        vm.pinSecurityCodeStatusAction.onNext(Unit)
        finishPaymentProcessingAction.assertValue(transaction)
    }

    @Test
    fun testReloadPaymentMethodsAction(){
        val paymentMethods: MutableList<PaymentMethod> = mutableListOf()
        paymentMethods.add(PaymentMethod( "123412312412312",  "1234",  "11/22",  "VISA"))
        paymentMethods.add(PaymentMethod( "3232323232",  "3232",  "12/23",  "MASTERCARD"))
        val vendor = Vendor( "7120-39345-1023841023-123434",  "Manpuku Sushi",  "Jaco")
        val model = CheckAndPayModel( "3c288f1b-e95f-40a2-8f53-40b61d356156",  1000.0,  100.0,  120.0,  2220.0,  0.0,  10,  13,  paymentMethods, vendor)
        setUpEnvironment(environment()!!)
        vm.inputs.reloadPaymentMethods(PaymentMethod("3232323232",  "3232",  "12/23",  "MASTERCARD"))
        reloadPaymentMethodsAction.assertValue(model)
    }

    @Test
    fun testAddPaymentMethodAction(){
        setUpEnvironment(environment()!!)
        vm.inputs.addPaymentMethodButtonPressed()
        addPaymentMethodAction.assertValueCount(1)
    }

    @Test
    fun testShowErrorNoCreditCard(){
        setUpFailEnvironment(environment()!!)
        vm.inputs.nextButtonPressed()
        val message = RPApplication.applicationContext().getString(
            R.string.Error_add_payment_method_first,
            contactEmail
        )
        showError.assertValue(message)
    }




    private fun setUpFailEnvironment(environment: Environment){
        val vendor = Vendor( "7120-39345-1023841023-123434",  "Manpuku Sushi",  "Jaco")
        val model = CheckAndPayModel( "3c288f1b-e95f-40a2-8f53-40b61d356156",  1000.0,  100.0,  120.0,  2220.0, 0.0,  10,  13,  mutableListOf(),  vendor)

        vm = PaymentSelectionVM.ViewModel(environment)
        vm.intent(Intent().putExtra("CheckAndPayModel",model))
        vm.outputs.addPaymentMethodAction().subscribe(addPaymentMethodAction)
        vm.outputs.paymentMethodChangedAction().subscribe(paymentMethodChangedAction)
        vm.outputs.showError().subscribe(showError)
        vm.outputs.finishPaymentProcessingAction().subscribe(finishPaymentProcessingAction)
        vm.outputs.nextButtonLoadingIndicator().subscribe(nextButtonLoadingIndicator)
        vm.outputs.applyRewards().subscribe(applyRewards)
        vm.outputs.checkAndPayModelAction().subscribe(checkAndPayModelAction)
        vm.outputs.reloadPaymentMethodsAction().subscribe(reloadPaymentMethodsAction)
    }
}