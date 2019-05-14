package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class SplashViewModelTest : RPTestCase(){
    lateinit var vm : SplashViewModel.ViewModel
    val result = TestObserver<UserStateResult>()


    private fun setUpEnvironment(environment: Environment){
        this.vm = SplashViewModel.ViewModel(environment)
        this.vm.outputs.splashAction().subscribe(this.result)
        this.vm.outputs.splashAction().subscribe {
            print(it.toString())
        }
    }

    @Test
    fun testResult(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.onCreate()
        val response = UserStateResult.signedOut
        result.assertValues(response)
    }

}