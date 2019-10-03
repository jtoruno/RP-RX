package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.Vendor
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.mocks.creditCardMock
import com.zimplifica.redipuntos.models.CheckAndPayModel
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import io.reactivex.observers.TestObserver
import org.junit.Test


class SPScanQRTest : RPTestCase() {
    lateinit var vm : SPScanQRVM.ViewModel
    val showError = TestObserver<String>()
    val nextScreenAction = TestObserver<CheckAndPayModel>()

    private fun setUpEnvironment(environment: Environment){
        vm = SPScanQRVM.ViewModel(environment)
        vm.outputs.nextScreenAction().subscribe(this.nextScreenAction)
        vm.outputs.showError().subscribe(this.showError)

    }

    @Test
    fun testGetVendorInformation(){
        val vendor = Vendor("7120-39345-1023841023-123434", "Manpuku Sushi","Jaco")
        val model = CheckAndPayModel("3c288f1b-e95f-40a2-8f53-40b61d356156", 4555.5, 0.0,1000.0, 5555.5, 1000.0, 10, 13,  creditCardMock, vendor)
        setUpEnvironment(environment()!!)
        vm.intent(Intent().putExtra("amount",5555.5))
        vm.inputs.codeFound("7120-39345-1023841023-123434")
        nextScreenAction.assertValue(model)
    }

    @Test
    fun testShowError(){
        setUpEnvironment(environment()!!)
        vm.inputs.codeFound("123")
        showError.assertValues("Código inválido, intente de nuevo.")
    }}