package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class ChangePasswordVMTest : RPTestCase(){
    lateinit var vm : ChangePasswordVM.ViewModel
    private val changePasswordButtonEnabled = TestObserver<Boolean>()
    private val changePasswordAction = TestObserver<Unit>()
    private val validPasswordLenght = TestObserver<Boolean>()
    private val validPasswordCapitalLowerLetters = TestObserver<Boolean>()
    private val validPasswordNumbers = TestObserver<Boolean>()
    private val validPasswordSpecialCharacters = TestObserver<Boolean>()
    private val showError = TestObserver<String>()
    private val loadingEnabled = TestObserver<Boolean>()

    private fun setUp(environment: Environment){
        vm = ChangePasswordVM.ViewModel(environment)
        vm.outputs.changePasswordAction().subscribe(this.changePasswordAction)
        vm.outputs.changePasswordButtonEnabled().subscribe(this.changePasswordButtonEnabled)
        vm.outputs.validPasswordLength().subscribe(this.validPasswordLenght)
        vm.outputs.validPasswordCapitalLowerLetters().subscribe(this.validPasswordCapitalLowerLetters)
        vm.outputs.validPasswordNumbers().subscribe(this.validPasswordNumbers)
        vm.outputs.validPasswordSpecialCharacters().subscribe(this.validPasswordSpecialCharacters)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
    }

    @Test
    fun testChangePasswordAction(){
        setUp(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.verificationCodeChange("123456")
        vm.inputs.newPasswordChanged("123Zimple!")
        vm.inputs.changePasswordButtonPressed()
        changePasswordAction.assertValueCount(1)
    }

    @Test
    fun testLoadingEnabled(){
        setUp(environment()!!)
        vm.inputs.onCreate()
        loadingEnabled.assertValues(true, false)


        vm.inputs.verificationCodeChange("666666")
        vm.inputs.newPasswordChanged("123Zimple23")
        vm.inputs.changePasswordButtonPressed()
        loadingEnabled.assertValues(true,false,true,false)
    }

    @Test
    fun testShowErrorAction(){
        setUp(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.verificationCodeChange( "666666")
        vm.inputs.newPasswordChanged( "123Zimple3222")
        vm.inputs.changePasswordButtonPressed()
        showError.assertValueCount(1)
    }

    @Test
    fun testValidatePasswordLenght(){
        setUp(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.newPasswordChanged("123")
        validPasswordLenght.assertValues( false)

        vm.inputs.newPasswordChanged( "123Jose_")
        validPasswordLenght.assertValues( false, true)

        vm.inputs.newPasswordChanged( "123Jose")
        validPasswordLenght.assertValues( false, true, false)
    }

    @Test
    fun testValidatePasswordNumbers(){
        setUp(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.newPasswordChanged( "abc")
        validPasswordNumbers.assertValues( false)

        vm.inputs.newPasswordChanged("abc1")
        validPasswordNumbers.assertValues( false, true)

        vm.inputs.newPasswordChanged( "abc")
        validPasswordNumbers.assertValues(false, true, false)
    }

    @Test
    fun testValidatePasswordCapitalLowerLetters(){
        setUp(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.newPasswordChanged( "abc")
        validPasswordCapitalLowerLetters.assertValues( false)

        vm.inputs.newPasswordChanged( "abcA")
        validPasswordCapitalLowerLetters.assertValues( false, true)

        vm.inputs.newPasswordChanged( "abc")
        validPasswordCapitalLowerLetters.assertValues( false, true, false)
    }

    @Test
    fun testValidatePasswordSpecialCharacters(){
        setUp(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.newPasswordChanged("abc")
        validPasswordSpecialCharacters.assertValues( false)

        vm.inputs.newPasswordChanged( "abc@")
        validPasswordSpecialCharacters.assertValues( false, true)

        vm.inputs.newPasswordChanged("abc")
        validPasswordSpecialCharacters.assertValues( false, true, false)
    }

    @Test
    fun testChangePasswordButtonEnabled(){
        setUp(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.verificationCodeChange( "123456")
        vm.inputs.newPasswordChanged( "123Zimple@")

        changePasswordButtonEnabled.assertValues( true)

        vm.inputs.verificationCodeChange( "12345")
        changePasswordButtonEnabled.assertValues( true, false)
    }

}