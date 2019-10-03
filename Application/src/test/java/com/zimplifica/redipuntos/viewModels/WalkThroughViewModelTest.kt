package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class WalkThroughViewModelTest : RPTestCase() {
    lateinit var vm : WalkThroughViewModel.ViewModel
    val startHelpActivity = TestObserver<Unit>()
    val startSignInActivity = TestObserver<Unit>()
    val startSignUpActivity = TestObserver<Unit>()

    private fun setUpEnvironment(environment : Environment){
        this.vm = WalkThroughViewModel.ViewModel(environment)

        this.vm.outputs.startHelpActivity().subscribe(this.startHelpActivity)
        this.vm.outputs.startSignInActivity().subscribe(this.startSignInActivity)
        this.vm.outputs.startSignUpActivity().subscribe(this.startSignUpActivity)
    }

    @Test
    fun startHelpActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.helpButtonClicked()
        this.startHelpActivity.assertValueCount(1)
    }

    @Test
    fun startSignInActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.signInButtonClicked()
        this.startSignInActivity.assertValueCount(1)
    }

    @Test
    fun startSignUpActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.signUpButtonClicked()
        this.startSignUpActivity.assertValueCount(1)
    }

}