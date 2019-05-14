package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class HomeViewModelTest : RPTestCase() {
    lateinit var vm : HomeViewModel.ViewModel
    val signOutAction = TestObserver<Unit>()


    private fun setUpEnvironment(environment: Environment){
        this.vm = HomeViewModel.ViewModel(environment)
        this.vm.outputs.signOutAction().subscribe(this.signOutAction)
    }

    @Test
    fun testSignOutAction(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.signOutButtonPressed()
        this.signOutAction.assertValueCount(1)
    }
}