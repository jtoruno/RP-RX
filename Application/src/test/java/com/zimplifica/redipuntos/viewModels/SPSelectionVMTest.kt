package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.Vendor
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import io.reactivex.observers.TestObserver
import org.junit.Test

class SPSelectionVMTest : RPTestCase() {
    lateinit var vm : SPSelectionVM.ViewModel
    val nextButtonAction = TestObserver<SitePaySellerSelectionObject>()
    val nextButtonEnabled = TestObserver<Boolean>()
    val vendorInformationAction = TestObserver<Vendor>()

    private fun setUpEnvironment(environment: Environment){
        vm = SPSelectionVM.ViewModel(environment)
        vm.outputs.nextButtonAction().subscribe(this.nextButtonAction)
        vm.outputs.nextButtonEnabled().subscribe(this.nextButtonEnabled)
        vm.outputs.vendorInformationAction().subscribe(this.vendorInformationAction)

    }

    @Test
    fun testNextButtonEnabled(){
        setUpEnvironment(environment()!!)
        vm.intent(Intent().putExtra("amount",5555.5F))
        val vendor = Vendor("7120-39345-1023841023-123434","Manpuku Sushi","Jaco")
        vm.inputs.vendorInformation(vendor)
        vm.inputs.descriptionTextFieldChanged("12")
        nextButtonEnabled.assertValues(true)
    }

    @Test
    fun testVendorInformationAction(){
        setUpEnvironment(environment()!!)
        vm.intent(Intent().putExtra("amount",5555.5F))
        val vendor = Vendor("7120-39345-1023841023-123434","Manpuku Sushi","Jaco")
        vm.inputs.vendorInformation(vendor)
        vendorInformationAction.assertValues(vendor)
    }

    @Test
    fun testNextButtonAction(){
        setUpEnvironment(environment()!!)
        vm.intent(Intent().putExtra("amount",5555.5F))
        val vendor = Vendor("7120-39345-1023841023-123434","Manpuku Sushi","Jaco")
        vm.inputs.descriptionTextFieldChanged("hello")
        vm.inputs.vendorInformation(vendor)
        vm.inputs.nextButtonPressed()
        nextButtonAction.assertValueCount(1)

    }

    @Test
    fun testAMount(){
        setUpEnvironment(environment()!!)
        vm.intent(Intent().putExtra("amount",5555.5F))
        val amount = vm.getAmount()
        assertEquals(5555.5F,amount)
    }


}