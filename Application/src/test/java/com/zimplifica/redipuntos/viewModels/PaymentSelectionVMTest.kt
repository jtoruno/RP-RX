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
    private val finishPaymentProcessingAction = TestObserver<Unit>()
    private val nextButtonLoadingIndicator = TestObserver<Boolean>()
    private val paymentInformationChangedAction = TestObserver<PaymentInformation>()

    private fun setUpEnvironment(environment: Environment){
        val payList = mutableListOf<PaymentMethod>()
        payList.add(PaymentMethod("1234321412341234",  "1234", "",  "visa",  4505.0, false))
        val vendor = Vendor("7120-39345-1023841023-123434", "Manpuku Sushi",  "Jaco")
        val item = Item("","", 5555.5)
        val order = Order("3c288f1b-e95f-40a2-8f53-40b61d356156", item, 0.0, 5555.5)
        val paymentPayload = PaymentPayload(1000.0, order, payList)
        val obj = SitePaySellerSelectionObject(vendor,paymentPayload)

        vm = PaymentSelectionVM.ViewModel(environment)
        val intent = Intent()
        intent.putExtra("amount",5555.5F)
        intent.putExtra("SPSelectionObject",obj)
        vm.intent(Intent().putExtra("amount",5555.5F))
        vm.outputs.paymentMethodChangedAction().subscribe(this.paymentMethodChangedAction)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.finishPaymentProcessingAction().subscribe(this.finishPaymentProcessingAction)
        vm.outputs.nextButtonLoadingIndicator().subscribe(this.nextButtonLoadingIndicator)
        vm.outputs.paymentInformationChangedAction().subscribe(this.paymentInformationChangedAction)
    }

    @Test
    fun testPaymentMethodChangedAction(){
        setUpEnvironment(environment()!!)
        val paymentMethod = PaymentMethod("22334455", "5530", "",  "mastercard",  2000.0,  false)
        vm.inputs.paymentMethodChanged(paymentMethod)
        paymentMethodChangedAction.assertValue(paymentMethod)
    }

    @Test
    fun testGetVendor(){
        setUpEnvironment(environment()!!)
        val vendor = vm.getgetVendor()
        assertNotNull(vendor)
    }
}