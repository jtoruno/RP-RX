package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class CompleteEmailVMTest: RPTestCase() {
    lateinit var vm : CompleteEmailVM.ViewModel
    val isEmailValid = TestObserver<Boolean>()
    val loading = TestObserver<Boolean>()
    val emailSuccessfullyConfirmed = TestObserver<String>()
    val showError = TestObserver<String>()

    private fun setUpEnvironment(environment: Environment){
        this.vm = CompleteEmailVM.ViewModel(environment)
        this.vm.outputs.isEmailValid().subscribe(this.isEmailValid)
        this.vm.outputs.loading().subscribe(this.loading)
        vm.outputs.emailSuccessfullyConfirmed().subscribe(this.emailSuccessfullyConfirmed)
        vm.outputs.showError().subscribe(this.showError)
    }

    @Test
    fun testIsEmailValid(){
        setUpEnvironment(environment()!!)
        vm.inputs.emailChanged("jose")
        this.isEmailValid.assertValues(false)
        vm.inputs.emailChanged("jose@gmail.com")
        this.isEmailValid.assertValues(false,true)
    }

    @Test
    fun testLoading(){
        setUpEnvironment(environment()!!)
        vm.inputs.emailChanged( "jose@gmail.com")
        vm.inputs.verifyEmailPressed()
        this.loading.assertValues(true, false)
    }

    @Test
    fun testEmailUpdated(){
        setUpEnvironment(environment()!!)
        vm.inputs.emailChanged("dsanchez@zimplifica.com")
        vm.inputs.verifyEmailPressed()
        this.emailSuccessfullyConfirmed.assertValueCount(1)
    }

    @Test
    fun testShowError(){
        setUpEnvironment(environment()!!)
        vm.inputs.emailChanged("a@zimplifica.com")
        vm.inputs.verifyEmailPressed()
        showError.assertValueCount(1)
    }

}