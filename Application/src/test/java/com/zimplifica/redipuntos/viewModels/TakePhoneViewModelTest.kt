package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.SignUpUsernameModel
import io.reactivex.observers.TestObserver
import org.junit.Test

class TakePhoneViewModelTest : RPTestCase() {
    lateinit var vm : TakePhoneViewModel.ViewModel
    val nextButtonIsEnabled = TestObserver<Boolean>()
    val starNextActivity = TestObserver<SignUpUsernameModel>()


    private fun setUpEnvironment(environment: Environment){
        this.vm = TakePhoneViewModel.ViewModel(environment)
        this.vm.outputs.startNextActivity().subscribe(this.starNextActivity)
        this.vm.outputs.nextButtonIsEnabled().subscribe(this.nextButtonIsEnabled)
    }

    @Test
    fun startNextActivity(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.phone("88888889")
        this.vm.inputs.nicknameChanged("admin")
        this.vm.inputs.nextButtonClicked()
        this.starNextActivity.assertValueCount(1)
    }

    @Test
    fun nextButtonIsEnabled(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.phone("12")
        this.vm.inputs.nicknameChanged("a")
        this.nextButtonIsEnabled.assertValues(false)
        this.vm.inputs.phone("8888888")
        this.vm.inputs.nicknameChanged("admin")
        this.nextButtonIsEnabled.assertValues(false, false,false)
        this.vm.inputs.phone("88888889")
        this.nextButtonIsEnabled.assertValues(false, false,false, true)
        this.vm.inputs.phone("18888889")
        this.nextButtonIsEnabled.assertValues(false, false, false,true, false)
    }
}