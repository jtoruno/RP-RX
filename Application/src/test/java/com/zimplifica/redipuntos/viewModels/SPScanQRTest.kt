package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.Vendor
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import io.reactivex.observers.TestObserver
import org.junit.Test


class SPScanQRTest : RPTestCase() {
    lateinit var vm : SPScanQRVM.ViewModel
    val showError = TestObserver<String>()
    val nextScreenAction = TestObserver<SitePaySellerSelectionObject>()

    private fun setUpEnvironment(environment: Environment){
        vm = SPScanQRVM.ViewModel(environment)
        vm.outputs.nextScreenAction().subscribe(this.nextScreenAction)
        vm.outputs.showError().subscribe(this.showError)

    }

    @Test
    fun testGetVendorInformation(){
        setUpEnvironment(environment()!!)
        vm.intent(Intent().putExtra("amount",555.0))
        vm.inputs.codeFound("7120-39345-1023841023-123434")
        nextScreenAction.assertValueCount(1)
        //val vendor = Vendor("7120-39345-1023841023-123434",  "Manpuku Sushi",  "Jaco")

    }

    @Test
    fun testShowError(){
        setUpEnvironment(environment()!!)
        vm.inputs.codeFound("123")
        showError.assertValues("Código inválido, intente de nuevo.")
    }}