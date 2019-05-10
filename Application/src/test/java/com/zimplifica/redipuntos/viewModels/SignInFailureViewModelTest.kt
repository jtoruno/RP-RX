package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class SignInFailureViewModelTest : RPTestCase() {
    lateinit var vm : SignInFailureViewModel.ViewModel
    val forgotPasswordButton = TestObserver<Unit>()
    val signUpButton = TestObserver<Unit>()

    private fun setUpEnvironment(environment: Environment){
        this.vm = SignInFailureViewModel.ViewModel(environment)

        this.vm.outputs.forgotPasswordButton().subscribe(this.forgotPasswordButton)
        this.vm.outputs.signUpButton().subscribe(this.signUpButton)
    }

    @Test
    fun testForgotPasswordButton(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.forgotPasswordButtonPressed()
        this.forgotPasswordButton.assertValueCount(1)
    }

    @Test
    fun testsignUpButton(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.signUpButtonPressed()
        this.signUpButton.assertValueCount(1)
    }
}