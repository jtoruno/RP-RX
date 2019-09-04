package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class LaunchVMTest : RPTestCase() {
    lateinit var vm : LaunchViewModel.ViewModel
    val nextScreen = TestObserver<UserInformationResult>()

    private fun setUpEnvironment(environment: Environment){
        this.vm = LaunchViewModel.ViewModel(environment)
        this.vm.outputs.nextScreen().subscribe {
            print(it.citizenId + it.userId)
        }
        this.vm.outputs.nextScreen().subscribe(this.nextScreen)

    }

    @Test
    fun testNextScreen(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.onCreate()
        /*
        val userInfo = UserInformationResult("e5a06e84-73f4-4a04-bcbc-a70552a4d92a", "115650044","PEDRO","FONSECA ARGUEDAS","10/10/1993","pedro@redi.com",
            "+50699443322", true, true, null,  0.0, mutableListOf())*/
        this.nextScreen.assertValueCount(1)
    }
}