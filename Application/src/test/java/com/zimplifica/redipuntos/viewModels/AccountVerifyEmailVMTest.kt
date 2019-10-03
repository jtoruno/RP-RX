package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.UserStatus
import com.zimplifica.domain.entities.VerificationStatus
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.mocks.userInformationMock
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class AccountVerifyEmailVMTest : RPTestCase() {
    lateinit var vm : AccountVerifyEmailVM.ViewModel
    private val validCode = TestObserver<Boolean>()
    private val verifyCodeAction = TestObserver<Unit>()
    private val loadingEnabled = TestObserver<Boolean>()
    private val showError = TestObserver<String>()


    private fun setUpEnvironment(environment: Environment){
        vm = AccountVerifyEmailVM.ViewModel(environment)
        vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.validCode().subscribe(this.validCode)
        vm.outputs.verifyCodeAction().subscribe(this.verifyCodeAction)
    }

    private fun setUpTest(){
        val currentUser = userInformationMock()
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
    }

    @Test
    fun testValidCode(){
        setUpTest()
        vm.inputs.codeValueChanged("")
        validCode.assertValues(false)
        vm.inputs.codeValueChanged("1234")
        validCode.assertValues(false,false)
        vm.inputs.codeValueChanged("949494")
        validCode.assertValues(false,false,true)
        vm.inputs.codeValueChanged("94942")
        validCode.assertValues(false,false,true,false)
    }

    @Test
    fun testVerifyCodeAction(){
        setUpTest()
        vm.inputs.codeValueChanged( "949494")
        vm.inputs.verifyCodePressed()
        verifyCodeAction.assertValueCount(1)
    }

    @Test
    fun testLoadingEnabled(){
        setUpTest()
        vm.inputs.codeValueChanged( "949494")
        vm.inputs.verifyCodePressed()
        loadingEnabled.assertValues(true,false)
    }

    @Test
    fun testShowError(){
        setUpTest()
        vm.inputs.codeValueChanged( "123456")
        vm.inputs.verifyCodePressed()
        showError.assertValueCount(1)
    }
}