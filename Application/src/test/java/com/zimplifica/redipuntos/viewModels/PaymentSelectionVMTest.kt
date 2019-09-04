package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import io.reactivex.observers.TestObserver
import org.junit.Test

class PaymentSelectionVMTest : RPTestCase() {
    lateinit var vm : PaymentSelectionVM.ViewModel
    private val paymentMethodChangedAction = TestObserver<PaymentMethod>()
    private val showError = TestObserver<String>()
    private val finishPaymentProcessingAction = TestObserver<Transaction>()
    private val nextButtonLoadingIndicator = TestObserver<Boolean>()
    private val paymentInformationChangedAction = TestObserver<PaymentInformation>()
    private val showVendor = TestObserver<Vendor>()

    private fun setUpEnvironment(environment: Environment){

        val payList = mutableListOf<PaymentMethod>()
        payList.add(PaymentMethod("1234321412341234",  "1234", "",  "visa",  4505.0, false))
        val vendor = Vendor("7120-39345-1023841023-123434", "Manpuku Sushi",  "Jaco")
        val item = Item("",5555.5)
        val order = Order("3c288f1b-e95f-40a2-8f53-40b61d356156", item, 0.0, 10.0,5555.5,5565.5,10,13)
        val paymentPayload = PaymentPayload(1000.0, order, payList)
        val obj = SitePaySellerSelectionObject(vendor,paymentPayload)

        vm = PaymentSelectionVM.ViewModel(environment)
        val intent = Intent()
        intent.putExtra("amount",5555.5F)
        intent.putExtra("SPSelectionObject",obj)
        vm.intent(intent)
        //vm = PaymentSelectionVM.ViewModel(environment)
        vm.outputs.paymentMethodChangedAction().subscribe(this.paymentMethodChangedAction)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.finishPaymentProcessingAction().subscribe(this.finishPaymentProcessingAction)
        vm.outputs.nextButtonLoadingIndicator().subscribe(this.nextButtonLoadingIndicator)
        vm.outputs.paymentInformationChangedAction().subscribe(this.paymentInformationChangedAction)
        vm.outputs.showVendor().subscribe(this.showVendor)
        vm.outputs.paymentInformationChangedAction().subscribe {
            println(it.rediPoints.toString() + " rewards" + it.cardAmountToPay +" total" +it.total )
        }
    }

    @Test
    fun testPaymentMethodChangedAction(){
        setUpEnvironment(environment()!!)
        val paymentMethod = PaymentMethod("22334455", "5530", "",  "mastercard",  2000.0,  false)
        vm.inputs.paymentMethodChanged(paymentMethod)
        paymentMethodChangedAction.assertValue(paymentMethod)
    }

    @Test
    fun testPaymentInformationChangedAction(){
        setUpEnvironment(environment()!!)
        val  paymentMethod = PaymentMethod("22334455", "5530",  "","mastercard", 2000.0, false)
        vm.inputs.paymentMethodChanged(paymentMethod)

        val  amount = vm.getAmount()

        //val  paymentInformation = PaymentInformation( 1000.0, paymentMethod.rewards,amount.toDouble())

        paymentInformationChangedAction.assertValueCount(1)

    }

    @Test
    fun testShowError(){
        setUpFail(environment()!!)
        val defaultPm = PaymentMethod("1234321412341234",  "1234", "",  "visa",  4505.0, false)
        vm.inputs.paymentMethodChanged(defaultPm)
        vm.inputs.nextButtonPressed()
        showError.assertValue("Ocurrió un error al procesar el pago. Por favor intente de nuevo.")
    }

    @Test
    fun testNextButtonLoadingIndicator(){
        setUpEnvironment(environment()!!)
        val defaultPm = PaymentMethod("1234321412341234",  "1234", "",  "visa",  4505.0, false)
        vm.inputs.paymentMethodChanged(defaultPm)
        vm.inputs.nextButtonPressed()
        nextButtonLoadingIndicator.assertValues(true,false)
    }

    @Test
    fun testFinishPaymentProcessingAction(){
        setUpEnvironment(environment()!!)
        val defaultPm = PaymentMethod("1234321412341234",  "1234", "",  "visa",  4505.0, false)
        vm.inputs.paymentMethodChanged(defaultPm)
        vm.inputs.nextButtonPressed()
        finishPaymentProcessingAction.assertValueCount(1)
    }


    @Test
    fun testShowVendor(){
        setUpEnvironment(environment()!!)
        showVendor.assertValueCount(1)
    }

    @Test
    fun testAmount(){
        setUpEnvironment(environment()!!)
        val amount = vm.getAmount()
        assertEquals(5555.5F,amount)
    }

    @Test
    fun testGetPaymentMethods(){
        setUpEnvironment(environment()!!)
        val paymentMethods = vm.getPaymentMethods()
        assertNotNull(paymentMethods)
    }

    private fun setUpFail(environment: Environment){
        val payList = mutableListOf<PaymentMethod>()
        payList.add(PaymentMethod("1234321412341234",  "1234", "",  "visa",  4505.0, false))
        val vendor = Vendor("7120-39345-1023841023-123434", "Manpuku Sushi",  "Jaco")
        val item = Item("", 5555.5)
        val order = Order("53448394-e95f-40a2-8f53-3343234", item, 0.0, 10.0,5555.5,5565.5,10,13)
        val paymentPayload = PaymentPayload(1000.0, order, payList)
        val obj = SitePaySellerSelectionObject(vendor,paymentPayload)

        vm = PaymentSelectionVM.ViewModel(environment)
        val intent = Intent()
        intent.putExtra("amount",5555.5F)
        intent.putExtra("SPSelectionObject",obj)
        vm.intent(intent)
        //vm = PaymentSelectionVM.ViewModel(environment)
        vm.outputs.paymentMethodChangedAction().subscribe(this.paymentMethodChangedAction)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.finishPaymentProcessingAction().subscribe(this.finishPaymentProcessingAction)
        vm.outputs.nextButtonLoadingIndicator().subscribe(this.nextButtonLoadingIndicator)
        vm.outputs.paymentInformationChangedAction().subscribe(this.paymentInformationChangedAction)
        vm.outputs.showVendor().subscribe(this.showVendor)
    }
}