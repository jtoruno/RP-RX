package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.SignUpError
import com.zimplifica.domain.entities.SignUpResult
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ErrorWrapper
import com.zimplifica.redipuntos.models.SignUpModel
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

    val verifyPhoneNumberAction = TestObserver<SignUpModel>()
    val showError = TestObserver<ErrorWrapper>()
    val loadingEnabled = TestObserver<Boolean>()

    private fun setUpEnviroment(environment: Environment){

        this.vm = PasswordViewModel.ViewModel(environment)
        this.vm.outputs.signUpButtonEnabled().subscribe(this.signUpButtonEnabled)
        this.vm.outputs.startTermsActivity().subscribe(this.startTermsActivity)
        this.vm.outputs.startPolicyActivity().subscribe(this.startPolicyActivity)

        this.vm.outputs.validPasswordLenght().subscribe(this.validPasswordLenght)
        this.vm.outputs.validPasswordCapitalLowerLetters().subscribe(this.validPasswordCapitalLowerLetters)
        this.vm.outputs.validPasswordNumbers().subscribe(this.validPasswordNumbers)
        this.vm.outputs.validPasswordSpecialCharacters().subscribe(this.validPasswordSpecialCharacters)

        this.vm.outputs.verifyPhoneNumberAction().subscribe(this.verifyPhoneNumberAction)
        this.vm.outputs.showError().subscribe(this.showError)
        this.vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
        this.vm.outputs.showError().subscribe {
            print(it.friendlyMessage)
        }

        this.vm.outputs.verifyPhoneNumberAction().subscribe {
            print(it.userId+" "+it.phoneNumber + " " + it.password)
        }
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
    @Test
    fun testVerifyPhoneNumberAction(){
        setUpEnviroment(environment()!!)
        //this.vm.inputs.username("88889999")
        this.vm.intent(Intent().putExtra("phone","88889999"))
        this.vm.inputs.password("123Jose_")
        this.vm.inputs.signUpButtonPressed()
        val uuid = this.vm.getUuid()
        val signUpModel = SignUpModel(uuid,"88889999","123Jose_")
        verifyPhoneNumberAction.assertValueCount(1)
    }

    @Test
    fun testSignedUpActionError(){
        setUpEnviroment(environment()!!)
        //this.vm.inputs.username("88889997")
        this.vm.intent(Intent().putExtra("phone","88889997"))
        this.vm.inputs.password("123Jose_")
        this.vm.inputs.signUpButtonPressed()
        val error = SignUpError.internalError("Invalid phone number")
        val wrapper = ErrorWrapper(error,"Ocurrió un error desconocido, por favor contacte a soporte@zimplifica.com")
        showError.assertValues(wrapper)
    }

    @Test
    fun testLoadingEnabled(){
        setUpEnviroment(environment()!!)
        //vm.inputs.username("88889999")
        this.vm.intent(Intent().putExtra("phone","88889999"))
        vm.inputs.password("123Jose_")
        vm.inputs.signUpButtonPressed()
        loadingEnabled.assertValues( true, false)
    }
}