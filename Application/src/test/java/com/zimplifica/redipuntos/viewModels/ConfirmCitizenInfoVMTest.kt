package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.Person
import io.reactivex.observers.TestObserver
import org.junit.Test

class ConfirmCitizenInfoVMTest : RPTestCase() {
    lateinit var vm : ConfirmCitizenInfoVM.ViewModel
    val printData = TestObserver.create<Person>()

    private fun setUpEnvirenment(environment: Environment){
        this.vm = ConfirmCitizenInfoVM.ViewModel(environment)
        this.vm.outputs.printData().subscribe(this.printData)
    }

    @Test
    fun testPrintData(){
        setUpEnvirenment(environment()!!)
        val p = Person()
        p.nombre = "Josue"
        vm.intent(Intent().putExtra("citizen",p))
        vm.inputs.onCreate()
        this.printData.assertValues(p)
    }
}