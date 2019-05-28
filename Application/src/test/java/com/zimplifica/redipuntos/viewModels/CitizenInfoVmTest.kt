package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.Person
import io.reactivex.observers.TestObserver
import org.junit.Test

class CitizenInfoVmTest : RPTestCase() {
    lateinit var vm : CitizenInfoVM.ViewModel
    val startScanActivity = TestObserver<Unit>()
    val startNextActivity = TestObserver<Person>()

    private fun setUpEnvironment(environment: Environment){
        this.vm = CitizenInfoVM.ViewModel(environment)
        this.vm.outputs.startNextActivity().subscribe(this.startNextActivity)
        this.vm.outputs.startScanActivity().subscribe(this.startScanActivity)
    }

    @Test
    fun testStartScanActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.nextButtonPressed()
        this.startScanActivity.assertValueCount(1)
    }

    @Test
    fun testStartNextActivity(){
        setUpEnvironment(environment()!!)
        val p = Person()
        this.vm.inputs.personInfo(p)
        this.startNextActivity.assertValues(p)
    }
}