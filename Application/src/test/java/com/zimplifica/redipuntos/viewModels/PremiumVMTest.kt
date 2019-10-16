package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class PremiumVMTest : RPTestCase() {
    lateinit var vm : PremiumVM.ViewModel
    val enablePremiumAction = TestObserver<Unit>()
    val termsAndConditionsAction = TestObserver<Unit>()

    private fun setUpEnv(environment: Environment){
        vm = PremiumVM.ViewModel(environment)
        vm.outputs.enablePremiumAction().subscribe(enablePremiumAction)
        vm.outputs.termsAndConditionsAction().subscribe(termsAndConditionsAction)
    }

    @Test
    fun testEnablePremiumAction(){
        setUpEnv(environment()!!)
        vm.inputs.enablePremiumButtonPressed()
        enablePremiumAction.assertValueCount(1)
    }

    @Test
    fun testTermsAndConditionsAction(){
        setUpEnv(environment()!!)
        vm.inputs.termsAndConditionsButtonPressed()
        termsAndConditionsAction.assertValueCount(1)
    }
}