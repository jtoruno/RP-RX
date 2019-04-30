package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class HelpViewModelTest : RPTestCase() {
    lateinit var vm : HelpViewModel.ViewModel
    val startTermsWebView = TestObserver<Unit>()
    val startPrivacyWebView = TestObserver<Unit>()

    private fun setUpEnviroment(environment: Environment){
        this.vm = HelpViewModel.ViewModel(environment)

        this.vm.outputs.startTermsWebView().subscribe(this.startTermsWebView)
        this.vm.outputs.startPrivacyWebView().subscribe(this.startPrivacyWebView)
    }

    @Test
    fun startTermsWebViewModel(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.termsButtonClicked()
        this.startTermsWebView.assertValueCount(1)
    }

    @Test
    fun startPrivacyWebViewModel(){
        setUpEnviroment(environment()!!)
        this.vm.inputs.privacyButtonClicked()
        this.startPrivacyWebView.assertValueCount(1)
    }
}