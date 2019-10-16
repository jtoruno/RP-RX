package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class PromoCodeVMTest : RPTestCase() {
    lateinit var vm : PromoCodeVM.ViewModel
    private val applyAction = TestObserver<Unit>()
    private val applyButtonEnabled = TestObserver<Boolean>()
    private val loading = TestObserver<Boolean>()

    private fun setUpEnv(environment: Environment){
        vm = PromoCodeVM.ViewModel(environment)
        vm.outputs.applyAction().subscribe(applyAction)
        vm.outputs.applyButtonEnabled().subscribe(applyButtonEnabled)
        vm.outputs.loading().subscribe(loading)
    }

    @Test
    fun testApplyAction(){
        setUpEnv(environment()!!)
        vm.inputs.applyButtonPressed("Test")
        applyAction.assertValueCount(1)
    }

    @Test
    fun testApplyButtonEnabled(){
        setUpEnv(environment()!!)
        vm.inputs.promoCodeChanged("123")
        applyButtonEnabled.assertValues(false)
        vm.inputs.promoCodeChanged("12345A")
        applyButtonEnabled.assertValues(false, true)
        vm.inputs.promoCodeChanged("1234")
        applyButtonEnabled.assertValues(false, true, false)
    }

    @Test
    fun testLoading(){
        setUpEnv(environment()!!)
        vm.inputs.applyButtonPressed("123Avc")
        loading.assertValues(true,false)
    }
}