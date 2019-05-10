package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.ForgotPasswordError
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ErrorWrapper
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test

class ForgotPasswordViewModelTest : RPTestCase() {

    lateinit var vm : ForgotPasswordViewModel.ViewModel
    val  nextButtonEnabled = TestObserver<Boolean>()
    val  loadingEnabled = TestObserver<Boolean>()
    val  showError = TestObserver<ErrorWrapper>()
    val  forgotPasswordStatus =  TestObserver<Unit>()

    fun setUpEnvironment(environment: Environment){
        vm = ForgotPasswordViewModel.ViewModel(environment)
        vm.outputs.nextButtonEnabled().subscribe(this.nextButtonEnabled)
        vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.forgotPasswordStatus().subscribe(this.forgotPasswordStatus)
    }

    @Test
    fun testNextButtonEnabled(){
        setUpEnvironment(environment()!!)
        vm.inputs.usernameChanged("55")
        nextButtonEnabled.assertValues(false)
        vm.inputs.usernameChanged("88889999")
        nextButtonEnabled.assertValues(false, true)
        vm.inputs.usernameChanged("8888")
        nextButtonEnabled.assertValues(false, true, false)
    }

    @Test
    fun testLoadingEnabled(){
        setUpEnvironment(environment()!!)
        vm.inputs.usernameChanged("88889999")
        vm.inputs.nextButtonPressed()
        loadingEnabled.assertValues(true, false)
    }

    @Test
    fun testShowError(){
        setUpEnvironment(environment()!!)
        vm.inputs.usernameChanged("99998888")
        vm.inputs.nextButtonPressed()
        val error = ForgotPasswordError.userNotFound
        val wrapper = ErrorWrapper(error, "El usuario ingresado no se encuetra registrado. Por favor intentar con un usuario válido.")
        showError.assertValues(wrapper)
    }

    @Test
    fun forgotPasswordStatus(){
        setUpEnvironment(environment()!!)
        vm.inputs.usernameChanged("88889999")
        vm.inputs.nextButtonPressed()
        this.forgotPasswordStatus.assertValueCount(1)

    }
}