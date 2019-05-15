package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class PayFragmentVMTest : RPTestCase() {
    lateinit var vm : PayFragmentVM.ViewModel
    val nextButtonEnabled = TestObserver<Boolean>()
    val changeAmountAction = TestObserver<String>()

    private fun setUpEnvironment(environment: Environment){
        this.vm = PayFragmentVM.ViewModel(environment)
        this.vm.outputs.changeAmountAction().subscribe(this.changeAmountAction)
        this.vm.outputs.nextButtonEnabled().subscribe(this.nextButtonEnabled)
        this.vm.outputs.changeAmountAction().subscribe {
            print(it+"\n")
        }
    }

    @Test
    fun testNextButtonEnabled(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.keyPressed("1")
        nextButtonEnabled.assertValues(true)
        this.vm.inputs.keyPressed("⬅")
        nextButtonEnabled.assertValues(true, false)

    }

    @Test
    fun testChangeAmountAction(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.keyPressed("2")
        changeAmountAction.assertValues("₡ 2.0")
        this.vm.inputs.keyPressed("5")
        changeAmountAction.assertValues("₡ 2.0","₡ 25.0")
        this.vm.inputs.keyPressed("5")
        changeAmountAction.assertValues("₡ 2.0","₡ 25.0","₡ 255.0")
        this.vm.inputs.keyPressed("5")
        changeAmountAction.assertValues("₡ 2.0","₡ 25.0","₡ 255.0","₡ 2,555.0")
        this.vm.inputs.keyPressed("⬅")
        changeAmountAction.assertValues("₡ 2.0","₡ 25.0","₡ 255.0","₡ 2,555.0","₡ 255.0")
    }
}