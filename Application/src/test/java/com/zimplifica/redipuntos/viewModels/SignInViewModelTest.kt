package com.zimplifica.redipuntos.viewModels

import io.reactivex.observers.TestObserver
import org.junit.Test

class SignInViewModelTest {
    lateinit var  vm : SignInViewModel.ViewModel
    val signInButtonIsEnabled = TestObserver<Boolean>()

    fun setup(){
        vm = SignInViewModel.ViewModel()
        vm.outputs.signInButtonIsEnabled().subscribe(this.signInButtonIsEnabled)
        vm.outputs.signInButtonIsEnabled().subscribe {
            print("Response $it")
        }
    }

    @Test
    fun testButtonEnabled(){
        setup()
        vm.inputs.username("")
        vm.inputs.password("")
        this.signInButtonIsEnabled.assertValues(false)
        vm.inputs.username("jtoru")
        this.signInButtonIsEnabled.assertValues(false, false)
        vm.inputs.password("123")
        this.signInButtonIsEnabled.assertValues(false, false, true)
    }
}