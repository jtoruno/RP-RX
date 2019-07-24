package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class SplashViewModelTest : RPTestCase(){
    lateinit var vm : SplashViewModel.ViewModel
    val signedOut = TestObserver<Unit>()
    val tokenAction = TestObserver<String>()


    private fun setUpEnvironment(environment: Environment){
        this.vm = SplashViewModel.ViewModel(environment)
        this.vm.outputs.signedOutAction().subscribe(this.signedOut)
        this.vm.outputs.tokenAction().subscribe(this.tokenAction)
    }

    @Test
    fun testResult(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.onCreate()
        signedOut.assertValueCount(1)
    }

    @Test
    fun testTokenAction(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.token("!12343434")
        tokenAction.assertValue("!12343434")
    }

    @Test
    fun testTokenError(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.token("")
    }
}