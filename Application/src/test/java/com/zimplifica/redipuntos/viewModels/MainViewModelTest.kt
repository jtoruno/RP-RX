package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class MainViewModelTest: RPTestCase() {
    lateinit var vm : MainViewModel.ViewModel
    val startSignInActivity = TestObserver<Unit>()
    val startSignUpActivity = TestObserver<Unit>()
    val startHelpActivity = TestObserver<Unit>()

    private fun setUpEnvironment(environment: Environment){
        this.vm = MainViewModel.ViewModel(environment)

        this.vm.outputs.startSignInActivity().subscribe(this.startSignInActivity)
        this.vm.outputs.startSignUpActivity().subscribe(this.startSignUpActivity)
        this.vm.outputs.startHelpActivity().subscribe(this.startHelpActivity)
    }

    @Test
    fun startSignInActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.signInButtonClicked()
        this.startSignInActivity.assertValueCount(1)
    }
    @Test
    fun startSignUnActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.signUpButtonClicked()
        this.startSignUpActivity.assertValueCount(1)
    }
    @Test
    fun startHelpActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.helpButtonClicked()
        this.startHelpActivity.assertValueCount(1)
    }

}