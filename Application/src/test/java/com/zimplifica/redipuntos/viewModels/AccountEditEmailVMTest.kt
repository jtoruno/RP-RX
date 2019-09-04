package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.UserStatus
import com.zimplifica.domain.entities.VerificationStatus
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class AccountEditEmailVMTest : RPTestCase() {
    lateinit var vm : AccountEditEmailVM.ViewModel
    private val verifyButtonEnabled = TestObserver<Boolean>()
    private val verifyEmailAction = TestObserver<String>()
    private val emailAction = TestObserver<String>()
    private val loadingEnabled = TestObserver<Boolean>()
    private val showError = TestObserver<String>()

    fun setUpEnvironment(environment: Environment){
        vm = AccountEditEmailVM.ViewModel(environment)
        vm.outputs.emailAction().subscribe(this.emailAction)
        vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.verifyButtonEnabled().subscribe(this.verifyButtonEnabled)
        vm.outputs.verifyEmailAction().subscribe(this.verifyEmailAction)
        vm.outputs.showError().subscribe {
            println(it)
        }
    }

    private fun setUpTest(email : String, state : Boolean){
        val status = UserStatus(VerificationStatus.Pending,null)
        val currentUser = UserInformationResult("", "",
            "PEDRO", "FONSECA SANCHEZ",
            "10/10/1994", email, "",
            false,  state,null, null, mutableListOf(), status)
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
    }

    @Test
    fun testVerifyButtonEnabled(){
        setUpTest("",false)
        vm.inputs.onCreated()
        vm.inputs.emailValueChanged("")
        verifyButtonEnabled.assertValues(false)
        vm.inputs.emailValueChanged("123")
        verifyButtonEnabled.assertValues(false,false)
        vm.inputs.emailValueChanged("zimple@zimple.com")
        verifyButtonEnabled.assertValues(false,false,true)
        vm.inputs.emailValueChanged("zimple@zimple")
        verifyButtonEnabled.assertValues(false,false,true,false)
    }

    @Test
    fun testEmailAction(){
        setUpTest("zimple@zimplifica.com",true)
        vm.inputs.onCreated()
        emailAction.assertValue("zimple@zimplifica.com")
    }

    @Test
    fun testLoadingEnabled(){
        setUpTest("",false)
        vm.inputs.onCreated()
        vm.inputs.emailValueChanged("zimple@zimple.com")
        vm.inputs.verifyEmailPressed()
        loadingEnabled.assertValues(true,false)
    }

    @Test
    fun testShowError(){
        setUpTest("",false)
        vm.inputs.onCreated()
        vm.inputs.emailValueChanged( "zimple@zimplifica.com")
        vm.inputs.verifyEmailPressed()
        showError.assertValueCount(1)
    }

    @Test
    fun testVerifyEmailActionAccountInfoIncomplete(){
        setUpTest("",false)
        vm.inputs.onCreated()
        vm.inputs.emailValueChanged( "dsanchez@zimplifica.com")
        vm.inputs.verifyEmailPressed()
        verifyEmailAction.assertValueCount(1)
    }

    @Test
    fun testVerifyEmailActionAccountInfoEmailVerified(){
        setUpTest("dsanchez@zimplifica.com",true)
        vm.inputs.onCreated()
        vm.inputs.emailValueChanged("dsanchez@zimplifica.com")
        vm.inputs.verifyEmailPressed()
        verifyEmailAction.assertValueCount(0)
    }

    @Test
    fun testVerifyEmailActionEmailChanged(){
        setUpTest("dsanchez@zimplifica.com",true)
        vm.inputs.onCreated()
        vm.inputs.emailValueChanged("jtoru@zimplifica.com")
        vm.inputs.verifyEmailPressed()
        verifyEmailAction.assertValueCount(1)
    }

}