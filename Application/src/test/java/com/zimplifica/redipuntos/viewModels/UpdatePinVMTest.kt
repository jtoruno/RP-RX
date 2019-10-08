package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.mocks.userInformationMock
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class UpdatePinVMTest : RPTestCase() {
    lateinit var vm : UpdatePinVM.ViewModel
    private val nextButtonAction = TestObserver<Boolean>()
    private val nextButtonEnabled = TestObserver<Boolean>()
    private val loadingEnabled = TestObserver<Boolean>()
    private val userInformationAction = TestObserver<UserInformationResult>()
    private val showErrorAction = TestObserver<String>()

    private fun setUp(environment: Environment){
        environment.currentUser().setCurrentUser(userInformationMock())
        vm = UpdatePinVM.ViewModel(environment)
        vm.outputs.nextButtonAction().subscribe(nextButtonAction)
        vm.outputs.nextButtonEnabled().subscribe(nextButtonEnabled)
        vm.outputs.loadingEnabled().subscribe(loadingEnabled)
        vm.outputs.userInformationAction().subscribe(userInformationAction)
        vm.outputs.showErrorAction().subscribe(showErrorAction)
    }

    @Test
    fun testUserInformationAction(){

        setUp(environment()!!)
        vm.inputs.viewDidLoad()
        userInformationAction.assertValue(userInformationMock())
    }

    @Test
    fun testNextButtonAction(){
        setUp(environment()!!)
        vm.inputs.viewDidLoad()
        vm.inputs.verificationCodeChanged( "123456")
        vm.inputs.pinChanged( "1234")

        vm.inputs.nextButtonPressed()

        nextButtonAction.assertValueCount(1)
    }

    @Test
    fun testNextButtonEnabled(){
        setUp(environment()!!)
        vm.inputs.viewDidLoad()
        vm.inputs.verificationCodeChanged( "123")

        vm.inputs.pinChanged("123")
        nextButtonEnabled.assertValues(false)

        vm.inputs.verificationCodeChanged( "123456")
        nextButtonEnabled.assertValues( false, false)

        vm.inputs.pinChanged( "1234")
        nextButtonEnabled.assertValues(false, false, true)

        vm.inputs.verificationCodeChanged( "12345")
        nextButtonEnabled.assertValues(false, false, true, false)
    }

    @Test
    fun testLoadingEnabled(){
        setUp(environment()!!)
        vm.inputs.viewDidLoad()
        vm.inputs.verificationCodeChanged( "123456")
        vm.inputs.pinChanged( "1234")

        vm.inputs.nextButtonPressed()

        loadingEnabled.assertValues( true, false, true, false)
    }

}