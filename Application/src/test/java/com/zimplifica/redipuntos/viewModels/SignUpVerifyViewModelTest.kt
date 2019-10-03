package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.domain.entities.SignUpError
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ErrorWrapper
import com.zimplifica.redipuntos.models.SignUpModel
import io.reactivex.observers.TestObserver
import org.junit.Test

class SignUpVerifyViewModelTest : RPTestCase(){
    lateinit var vm : SignUpVerifyViewModel.ViewModel
    val verificationButtonEnabled = TestObserver<Boolean>()
    val verifiedAction = TestObserver<Unit>()
    val resendAction = TestObserver<Unit>()
    val showError = TestObserver<ErrorWrapper>()
    val loadingEnabled = TestObserver<Boolean>()

    private val userId = "E621E1F8-C36C-495A-93FC-0C247A3E6E5F"
    private val phoneNumber = "88889999"
    private val password = "123Jose_"
    private val nickname = "Jtoru"

    private fun setUpEnvironment(environment: Environment){
        this.vm = SignUpVerifyViewModel.ViewModel(environment)
        val model = SignUpModel(userId,phoneNumber,password,nickname)
        this.vm.intent(Intent().putExtra("SignUpModel",model))
        //environment.sharedPreferences().edit().putString("userId","E621E1F8-C36C-495A-93FC-0C247A3E6E5F").apply()
        //environment.sharedPreferences().edit().putString("phoneNumber","+50688889999").apply()
        //environment.sharedPreferences().edit().putString("password","123Jose").apply()
        this.vm.outputs.verificationButtonEnabled().subscribe(this.verificationButtonEnabled)
        this.vm.outputs.verifiedAction().subscribe(this.verifiedAction)
        this.vm.outputs.resendAction().subscribe(this.resendAction)
        this.vm.outputs.showError().subscribe(this.showError)
        this.vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
    }

    private fun setUpFailureEnvironment(environment: Environment){
        this.vm = SignUpVerifyViewModel.ViewModel(environment)
        val model = SignUpModel( "E621E1F8-C36C-495A-93FC-0C247A3E6E5F", "88889995",  "asdq1131", nickname)
        this.vm.intent(Intent().putExtra("SignUpModel",model))
        //environment.sharedPreferences().edit().putString("userId","E621E1F8-C36C-495A-93FC-0C247A3E6E5F").apply()
        //environment.sharedPreferences().edit().putString("phoneNumber","+50688889999").apply()
        //environment.sharedPreferences().edit().putString("password","123Jose").apply()
        this.vm.outputs.verificationButtonEnabled().subscribe(this.verificationButtonEnabled)
        this.vm.outputs.verifiedAction().subscribe(this.verifiedAction)
        this.vm.outputs.resendAction().subscribe(this.resendAction)
        this.vm.outputs.showError().subscribe(this.showError)
        this.vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
        this.vm.outputs.showError().subscribe {
            print(it.friendlyMessage)
        }
    }

    @Test
    fun testGetuserName(){
        setUpEnvironment(environment()!!)
        val username = this.vm.username
        assertEquals("88889999",username)
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
        //vm.inputs.verificationCodeTextChanged( "123456")
        this.vm.inputs.resendVerificationCodePressed()
        resendAction.assertValueCount(1)
    }

    @Test
    fun testResendButtonActionUnsuccessful(){

        setUpFailureEnvironment(environment()!!)
        //vm.inputs.verificationCodeTextChanged( "123456")

        this.vm.inputs.resendVerificationCodePressed()
        val error = SignUpError.internalError("Invalid phone number")
        val wrapper = ErrorWrapper(error,"Ocurrió un error desconocido, por favor contacte a soporte@zimplifica.com")
        this.showError.assertValues(wrapper)

    }}