package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class PasswordViewModelTest : RPTestCase() {
    lateinit var vm : PasswordViewModel.ViewModel
    val signUpButtonEnabled = TestObserver<Boolean>()
    val startTermsActivity = TestObserver<Unit>()
    val startPolicyActivity = TestObserver<Unit>()

    val validPasswordLenght = TestObserver<Boolean>()
    val validPasswordCapitalLowerLetters = TestObserver<Boolean>()
    val validPasswordNumbers = TestObserver<Boolean>()
    val validPasswordSpecialCharacters = TestObserver<Boolean>()

    private fun setUpEnviroment(environment: Environment){
        this.vm = PasswordViewModel.ViewModel(environment)
        this.vm.outputs.signUpButtonEnabled().subscribe(this.signUpButtonEnabled)
        this.vm.outputs.startTermsActivity().subscribe(this.startTermsActivity)
        this.vm.outputs.startPolicyActivity().subscribe(this.startPolicyActivity)

        this.vm.outputs.validPasswordLenght().subscribe(this.validPasswordLenght)
        this.vm.outputs.validPasswordCapitalLowerLetters().subscribe(this.validPasswordCapitalLowerLetters)
        this.vm.outputs.validPasswordNumbers().subscribe(this.validPasswordNumbers)
        this.vm.outputs.validPasswordSpecialCharacters().subscribe(this.validPasswordSpecialCharacters)
    }

    @Test
    fun startActivities(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.termsAndConditionsButtonPressed()
        this.startTermsActivity.assertValueCount(1)
        this.vm.inputs.privacyPolicyButtonPressed()
        this.startPolicyActivity.assertValueCount(1)
    }

    @Test
    fun buttonEnabled(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.password("123")
        this.signUpButtonEnabled.assertValues(false)
        this.vm.inputs.password("123Zimple")
        this.signUpButtonEnabled.assertValues(false, false)
        this.vm.inputs.password("123Zimple$")
        this.signUpButtonEnabled.assertValues(false, false, true)
    }

    @Test
    fun passLenght(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.password("123")
        this.validPasswordLenght.assertValues(false)
        this.vm.inputs.password("12345678")
        this.validPasswordLenght.assertValues(false, true)
        this.vm.inputs.password("12345678qwertyuiopasd")
        this.validPasswordLenght.assertValues(false, true, false)
        this.vm.inputs.password("12345678qwertyuiopas")
        this.validPasswordLenght.assertValues(false, true, false, true)
    }

    @Test
    fun passCapitalLower(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.password("aaa")
        this.validPasswordCapitalLowerLetters.assertValues(false)
        this.vm.inputs.password("aaaB")
        this.validPasswordCapitalLowerLetters.assertValues(false, true)
    }

    @Test
    fun passNumbers(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.password("aaa")
        this.validPasswordNumbers.assertValues(false)
        this.vm.inputs.password("aaa1")
        this.validPasswordNumbers.assertValues(false, true)
    }

    @Test
    fun passSpecialCharacters(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.password("aaa")
        this.validPasswordSpecialCharacters.assertValues(false)
        this.vm.inputs.password("aaa$$")
        this.validPasswordSpecialCharacters.assertValues(false, true)

    }

}