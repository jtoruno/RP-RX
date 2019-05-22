package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.SignUpError
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ErrorWrapper
import io.reactivex.observers.TestObserver
import org.junit.Test

class SignUpVerifyViewModelTest : RPTestCase(){
    lateinit var vm : SignUpVerifyViewModel.ViewModel
    val verificationButtonEnabled = TestObserver<Boolean>()
    val verifiedAction = TestObserver<Unit>()
    val resendAction = TestObserver<Unit>()
    val showError = TestObserver<ErrorWrapper>()
    val loadingEnabled = TestObserver<Boolean>()


    private fun setUpEnvironment(environment: Environment){
        this.vm = SignUpVerifyViewModel.ViewModel(environment)
        //environment.sharedPreferences().edit().putString("userId","E621E1F8-C36C-495A-93FC-0C247A3E6E5F").apply()
        //environment.sharedPreferences().edit().putString("phoneNumber","+50688889999").apply()
        //environment.sharedPreferences().edit().putString("password","123Jose").apply()
        this.vm.username = "88889999"
        this.vm.userId = "E621E1F8-C36C-495A-93FC-0C247A3E6E5F"
        this.vm.password = "123Jose_"
        this.vm.outputs.verificationButtonEnabled().subscribe(this.verificationButtonEnabled)
        this.vm.outputs.verifiedAction().subscribe(this.verifiedAction)
        this.vm.outputs.resendAction().subscribe(this.resendAction)
        this.vm.outputs.showError().subscribe(this.showError)
        this.vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
    }

    @Test
    fun testGetuserName(){
        setUpEnvironment(environment()!!)
        //val n = environment()!!.sharedPreferences().getString("phoneNumber","")
        val username = this.vm.getUserName()
        assertEquals("88889999",username)
    }

    @Test
    fun testGetUserId(){
        setUpEnvironment(environment()!!)
        val uuid = this.vm.getUserUUid()
        assertEquals("E621E1F8-C36C-495A-93FC-0C247A3E6E5F", uuid)
    }

    @Test
    fun testVerifyButtonEnabled(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.verificationCodeTextChanged("1")
        this.verificationButtonEnabled.assertValues(false)
        this.vm.inputs.verificationCodeTextChanged("123456")
        this.verificationButtonEnabled.assertValues(false, true)
        this.vm.inputs.verificationCodeTextChanged("1234")
        this.verificationButtonEnabled.assertValues(false, true, false)
    }

    @Test
    fun testUserVerifiedActionSuccessful(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.verificationCodeTextChanged("123456")
        this.vm.inputs.verificationButtonPressed()
        verifiedAction.assertValueCount(1)
    }

    @Test
    fun testLoadingEnabled(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.verificationCodeTextChanged("123456")
        this.vm.inputs.verificationButtonPressed()
        this.loadingEnabled.assertValues(true, false)
    }

    @Test
    fun testUserVerifiedActionUnsuccessful(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.verificationCodeTextChanged("6666554")
        this.vm.inputs.verificationButtonPressed()
        val error = SignUpError.usernameExistsException
        val wrapper = ErrorWrapper(error,"El usuario ingresado está actualmente registrado en el sistema.")
        showError.assertValues(wrapper)
    }

    @Test
    fun testResendButtonActionSuccessful(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.resendVerificationCodePressed()
        resendAction.assertValueCount(1)
    }

    @Test
    fun testResendButtonActionUnsuccessful(){

        setUpEnvironment(environment()!!)
        this.vm.userId = "E621E1F8-C36C-495A-93FC-0C247A3WRONG"
        this.vm.inputs.resendVerificationCodePressed()
        val error = SignUpError.tooManyRequestsException
        val wrapper = ErrorWrapper(error,"Has alcanzado el máximo de intentos. Por favor intenta más tarde.")
        this.showError.assertValues(wrapper)

    }}