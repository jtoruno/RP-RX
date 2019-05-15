package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class HomeViewModelTest : RPTestCase() {
    lateinit var vm : HomeViewModel.ViewModel
    val signOutAction = TestObserver<Unit>()
    //val nextButtonEnabled = TestObserver<Boolean>()
    //val changeAmountAction = TestObserver<String>()


    private fun setUpEnvironment(environment: Environment){
        this.vm = HomeViewModel.ViewModel(environment)
        this.vm.outputs.signOutAction().subscribe(this.signOutAction)

        //this.vm.outputs.nextButtonEnabled().subscribe(this.nextButtonEnabled)
        //this.vm.outputs.changeAmountAction().subscribe(this.changeAmountAction)

    }

    @Test
    fun testSignOutAction(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.signOutButtonPressed()
        this.signOutAction.assertValueCount(1)
    }
    /*
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
        changeAmountAction.assertValues("₡2.0")
        this.vm.inputs.keyPressed("5")
        changeAmountAction.assertValues("₡2.0","₡25.0")
        this.vm.inputs.keyPressed("5")
        changeAmountAction.assertValues("₡2.0","₡25.0","₡255.0")
        this.vm.inputs.keyPressed("5")
        changeAmountAction.assertValues("₡2.0","₡25.0","₡255.0","₡2,555.0")
        this.vm.inputs.keyPressed("⬅")
        changeAmountAction.assertValues("₡2.0","₡25.0","₡255.0","₡2,555.0","₡255.0")
    }*/
}