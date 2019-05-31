package com.zimplifica.redipuntos.viewModels

import android.content.Intent
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class ConfirmEmailVMTest : RPTestCase() {
    lateinit var vm : ConfirmEmailVM.ViewModel
    val email = TestObserver<String>()
    val loading = TestObserver<Boolean>()
    val confirmedEmail = TestObserver<Unit>()
    val showError = TestObserver<String>()
    val codeResent = TestObserver<Unit>()
    val isButtonEnabled = TestObserver<Boolean>()
    val showResendAlert = TestObserver<Unit>()
    //val resendCode = TestObserver<Unit>()


    private fun setUpEnvironment(environment: Environment){
        vm = ConfirmEmailVM.ViewModel(environment)
        vm.outputs.email().subscribe(this.email)
        vm.outputs.loading().subscribe(this.loading)
        vm.outputs.confirmedEmail().subscribe(this.confirmedEmail)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.codeResent().subscribe(this.codeResent)
        vm.outputs.isButtonEnabled().subscribe(this.isButtonEnabled)
        vm.outputs.showResendAlert().subscribe(this.showResendAlert)
    }

    @Test
    fun testEmail(){
        setUpEnvironment(environment()!!)
        vm.intent(Intent().putExtra("email","josedani.04.24@gmail.com"))
        this.email.assertValue("josedani.04.24@gmail.com")
    }

    @Test
    fun testLoading(){
        setUpEnvironment(environment()!!)
        vm.inputs.verificationCodeChanged("9495")
        vm.inputs.confirmEmailButtonPressed()
        loading.assertValues(true,false)

    }
    @Test
    fun testConfirmedEmail() {
        setUpEnvironment(environment()!!)
        vm.inputs.verificationCodeChanged("949494")
        vm.inputs.confirmEmailButtonPressed()
        confirmedEmail.assertValueCount(1)
    }

    @Test
    fun testShowError() {
        setUpEnvironment(environment()!!)
        vm.inputs.verificationCodeChanged( "959444")
        vm.inputs.confirmEmailButtonPressed()
        showError.assertValueCount(1)
    }
    @Test
    fun testIsButtonEnabled() {
        setUpEnvironment(environment()!!)
        vm.inputs.verificationCodeChanged("55004")
        isButtonEnabled.assertValues(false)
        vm.inputs.verificationCodeChanged("123456")
        isButtonEnabled.assertValues(false,true)
    }

    @Test
    fun testShowResendAlert() {
        setUpEnvironment(environment()!!)
        vm.inputs.resendCodeButtonPressed()
        showResendAlert.assertValueCount(1)
    }

    @Test
    fun testCodeResend() {
        setUpEnvironment(environment()!!)
        vm.inputs.resendCodeButtonPressed()
        showResendAlert.assertValueCount(1)
        vm.inputs.confirmResendButtonPressed()
        codeResent.assertValueCount(1)
    }
}