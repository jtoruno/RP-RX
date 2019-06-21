package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class AccountInfoVMTest : RPTestCase() {
    lateinit var vm : AccountInfoVM.ViewModel
    val verifyEmailAction = TestObserver<Unit>()
    val userInformationAction = TestObserver<UserInformationResult>()

    private fun setUpEnvironment(environment: Environment){
        vm = AccountInfoVM.ViewModel(environment)
        vm.outputs.userInformationAction().subscribe(this.userInformationAction)
        vm.outputs.verifyEmailAction().subscribe(this.verifyEmailAction)
    }

    @Test
    fun testVerifyEmailAction(){
        val currentUser = UserInformationResult("550e8400-e29b-41d4-a716-446655440000", "11565O433",
            "José", "Sanchez",
            "10/10/1994", "josedani.04.24@gmail.com", "+50686137284",
            true,  false,null, null, mutableListOf())
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
        vm.inputs.viewCreated()
        vm.inputs.verifyEmailPressed()
        verifyEmailAction.assertValueCount(1)
    }

    @Test
    fun testUserInformationAction(){
        val currentUser = UserInformationResult("550e8400-e29b-41d4-a716-446655440000", "11565O433",
            "José", "Sanchez",
            "10/10/1994", "josedani.04.24@gmail.com", "+50686137284",
            true,  false,null, null, mutableListOf())
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
        vm.inputs.viewCreated()
        userInformationAction.assertValue(currentUser)
    }
}