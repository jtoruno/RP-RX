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
    val  showError = TestObserver<String>()
    val  forgotPasswordStatus =  TestObserver<String>()

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
        vm.inputs.usernameChanged("88888888")
        vm.inputs.nextButtonPressed()
        //val error = ForgotPasswordError.unknown
        //val wrapper = ErrorWrapper(error, "Ocurrió un error desconocido, por favor contacte a soporte@zimplifica.com)
        showError.assertValueCount(1)
    }

    @Test
    fun forgotPasswordStatus(){
        setUpEnvironment(environment()!!)
        vm.inputs.usernameChanged("88889999")
        vm.inputs.nextButtonPressed()
        this.forgotPasswordStatus.assertValueCount(1)

    }
}