package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.Vendor
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class SPScanQRTest : RPTestCase() {
    lateinit var vm : SPScanQRVM.ViewModel
    val showError = TestObserver<String>()
    val getVendorInformationAction = TestObserver<Vendor>()

    private fun setUpEnvironment(environment: Environment){
        vm = SPScanQRVM.ViewModel(environment)
        vm.outputs.getVendorInformationAction().subscribe(this.getVendorInformationAction)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.getVendorInformationAction().subscribe {
            print(it.address+  it.name+ it.pk)
        }

    }

    @Test
    fun testGetVendorInformation(){
        setUpEnvironment(environment()!!)
        vm.inputs.codeFound("7120-39345-1023841023-123434")
        //val vendor = Vendor("7120-39345-1023841023-123434",  "Manpuku Sushi",  "Jaco")
        getVendorInformationAction.assertValueCount(1)
    }

    @Test
    fun testShowError(){
        setUpEnvironment(environment()!!)
        vm.inputs.codeFound("123")
        showError.assertValues("Código inválido, intente de nuevo.")
    }}