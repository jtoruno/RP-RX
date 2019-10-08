package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class VerifyPinVMTest : RPTestCase() {
    lateinit var vm : VerifyPinVM.ViewModel
    private val nextButtonAction = TestObserver<Boolean>()
    private val nextButtonEnabled = TestObserver<Boolean>()

    private val loadingEnabled = TestObserver<Boolean>()

    private val showErrorAction = TestObserver<String>()

    private fun setUp(environment: Environment){
        vm = VerifyPinVM.ViewModel(environment)
        vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
        vm.outputs.nextButtonAction().subscribe(this.nextButtonAction)
        vm.outputs.nextButtonEnabled().subscribe(this.nextButtonEnabled)
        vm.outputs.showErrorAction().subscribe(this.showErrorAction)
    }

    @Test
    fun testNextButtonAction(){
        setUp(environment()!!)
        vm.inputs.pinChanged("1235")
        vm.inputs.nextButtonPressed()
        nextButtonAction.assertValueCount(1)
    }

    @Test
    fun testNextButtonEnabled() {
        setUp(environment()!!)
        vm.inputs.pinChanged("123")
        nextButtonEnabled.assertValues(false)

        vm.inputs.pinChanged("1234")
        nextButtonEnabled.assertValues(false, true)

        vm.inputs.pinChanged("123")
        nextButtonEnabled.assertValues(false, true, false)
    }

    @Test
    fun testLoadingEnabled(){
        setUp(environment()!!)
        vm.inputs.pinChanged("1233")
        vm.inputs.nextButtonPressed()

        loadingEnabled.assertValues( true, false)
    }

    @Test
    fun testShowError(){
        setUp(environment()!!)
        vm.inputs.pinChanged("1234")
        vm.inputs.nextButtonPressed()
        showErrorAction.assertValueCount(1)
    }}