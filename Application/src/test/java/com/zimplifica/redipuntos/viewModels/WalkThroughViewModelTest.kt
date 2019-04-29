package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class WalkThroughViewModelTest : RPTestCase() {
    lateinit var vm : WalkThroughViewModel.ViewModel
    val startNextActivity = TestObserver<Unit>()
    val startMainActivity = TestObserver<Unit>()
    val startBackActivity = TestObserver<Unit>()

    private fun setUpEnvironment(environment : Environment){
        this.vm = WalkThroughViewModel.ViewModel(environment)

        this.vm.outputs.startMainActivity().subscribe(this.startMainActivity)
        this.vm.outputs.startNextActivity().subscribe(this.startNextActivity)
        this.vm.outputs.startBackActivity().subscribe(this.startBackActivity)
    }

    @Test
    fun startMainActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.skipButtonClicked()
        this.startMainActivity.assertValueCount(1)
    }

    @Test
    fun startBackActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.backButtonClicked()
        this.startBackActivity.assertValueCount(1)
    }

    @Test
    fun startNextActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.nextButtonClicked()
        this.startNextActivity.assertValueCount(1)
    }

}