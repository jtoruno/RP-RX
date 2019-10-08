package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.ForgotPasswordError
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ErrorWrapper
import io.reactivex.observers.TestObserver
import org.junit.Test

class ConfirmForgotPsswordVMTest : RPTestCase() {
    lateinit var vm : ConfirmForgotPsswordVM.ViewModel
    val nextButtonEnabled = TestObserver<Boolean>()
    val passwordChangedAction = TestObserver<Boolean>()
    val showError = TestObserver<String>()
    val loadingEnabled = TestObserver<Boolean>()

    val validPasswordLenght = TestObserver<Boolean>()
    val validPasswordCapitalLowerLetters = TestObserver<Boolean>()
    val validPasswordNumbers = TestObserver<Boolean>()
    val validPasswordSpecialCharacters = TestObserver<Boolean>()

    private fun setUpEnviroment(environment: Environment){
        this.vm = ConfirmForgotPsswordVM.ViewModel(environment)
        vm.intent(Intent().putExtra("username","+50688889999"))
        this.vm.outputs.nextButtonEnabled().subscribe(this.nextButtonEnabled)
        this.vm.outputs.passwordChangedAction().subscribe(this.passwordChangedAction)
        this.vm.outputs.showError().subscribe(this.showError)
        this.vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)

        this.vm.outputs.validPasswordLenght().subscribe(this.validPasswordLenght)
        this.vm.outputs.validPasswordCapitalLowerLetters().subscribe(this.validPasswordCapitalLowerLetters)
        this.vm.outputs.validPasswordNumbers().subscribe(this.validPasswordNumbers)
        this.vm.outputs.validPasswordSpecialCharacters().subscribe(this.validPasswordSpecialCharacters)
    }

    @Test
    fun testNextButtonEnabled(){
        setUpEnviroment(environment()!!)
        vm.inputs.confirmationCodeTextChanged("1234")
        vm.inputs.passwordChanged("123Jose_")
        nextButtonEnabled.assertValues(false)
        vm.inputs.confirmationCodeTextChanged("123456")
        nextButtonEnabled.assertValues(false,true)
        vm.inputs.confirmationCodeTextChanged("1234568")
        nextButtonEnabled.assertValues(false,true, false)
    }

    @Test
    fun testPasswordChangedAction(){
        setUpEnviroment(environment()!!)
        vm.inputs.passwordChanged("123Jose_")
        this.vm.inputs.confirmationCodeTextChanged("123456")
        vm.inputs.nextButtonPressed()
        passwordChangedAction.assertValueCount(1)
    }

    @Test
    fun testShowError(){
        setUpEnviroment(environment()!!)
        vm.inputs.passwordChanged("123abc#")
        this.vm.inputs.confirmationCodeTextChanged("123456")
        vm.inputs.nextButtonPressed()
        showError.assertValueCount(1)
    }

    @Test
    fun passLenght(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.passwordChanged("123")
        this.validPasswordLenght.assertValues(false)
        this.vm.inputs.passwordChanged("12345678")
        this.validPasswordLenght.assertValues(false, true)
        this.vm.inputs.passwordChanged("12345678qwertyuiopasd")
        this.validPasswordLenght.assertValues(false, true, false)
        this.vm.inputs.passwordChanged("12345678qwertyuiopas")
        this.validPasswordLenght.assertValues(false, true, false, true)
    }

    @Test
    fun passCapitalLower(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.passwordChanged("aaa")
        this.validPasswordCapitalLowerLetters.assertValues(false)
        this.vm.inputs.passwordChanged("aaaB")
        this.validPasswordCapitalLowerLetters.assertValues(false, true)
    }

    @Test
    fun passNumbers(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.passwordChanged("aaa")
        this.validPasswordNumbers.assertValues(false)
        this.vm.inputs.passwordChanged("aaa1")
        this.validPasswordNumbers.assertValues(false, true)
    }

    @Test
    fun passSpecialCharacters(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.passwordChanged("aaa")
        this.validPasswordSpecialCharacters.assertValues(false)
        this.vm.inputs.passwordChanged("aaa$$")
        this.validPasswordSpecialCharacters.assertValues(false, true)
    }

    @Test
    fun testLoadingEnabled(){
        setUpEnviroment(environment()!!)
        vm.inputs.passwordChanged("123Jose_")
        this.vm.inputs.confirmationCodeTextChanged("123456")
        vm.inputs.nextButtonPressed()
        loadingEnabled.assertValues(true, false)

    }

}