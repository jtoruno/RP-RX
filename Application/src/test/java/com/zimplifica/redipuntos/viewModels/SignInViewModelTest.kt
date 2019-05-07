package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.SignInError
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ErrorWrapper
import io.reactivex.observers.TestObserver
import org.junit.Test

class SignInViewModelTest : RPTestCase(){
    lateinit var  vm : SignInViewModel.ViewModel
    val signInButtonIsEnabled = TestObserver<Boolean>()
    val loadingEnabled = TestObserver<Boolean>()
    val showError = TestObserver<ErrorWrapper>()
    val signedInAction = TestObserver<Unit>()
    val showConfirmationAlert = TestObserver<Unit>()


    fun setup(environment: Environment){
        vm = SignInViewModel.ViewModel(environment)
        print(environment.webEndpoint())
        vm.outputs.loadingEnabled().subscribe(this.loadingEnabled)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.signedInAction().subscribe(this.signedInAction)
        vm.outputs.showConfirmationAlert().subscribe(this.showConfirmationAlert)
        vm.outputs.signInButtonIsEnabled().subscribe(this.signInButtonIsEnabled)
        /*
        vm.outputs.signInButtonIsEnabled().subscribe {
            print("Response $it")
        }*/
        vm.outputs.showError().subscribe {
            print("ErrorWrapper"+it.friendlyMessage)
        }
    }

    @Test
    fun testButtonEnabled(){
        setup(environment()!!)
        vm.inputs.username("")
        vm.inputs.password("")
        this.signInButtonIsEnabled.assertValues(false)
        vm.inputs.username("jtoru@zimplifica.com")
        this.signInButtonIsEnabled.assertValues(false, false)
        vm.inputs.password("123Jose_")
        this.signInButtonIsEnabled.assertValues(false, false, true)
        vm.inputs.username("89626004")
        this.signInButtonIsEnabled.assertValues(false, false, true, true)
    }

    @Test
    fun testShowErrorMessage(){
        setup(environment()!!)
        vm.inputs.username("jtoru@zimplifica.com")
        vm.inputs.password("pass123Wdsdsd_")
        vm.inputs.signInButtonPressed()
        val error = SignInError.invalidCredentials
        val wrapper = ErrorWrapper(error,"Las credenciales son inválidas")
        this.showError.assertValues(wrapper)
    }

    @Test
    fun testSignInAction(){
        setup(environment()!!)
        vm.inputs.username("88889999")
        vm.inputs.password("123Jose_")
        vm.inputs.signInButtonPressed()
        this.signedInAction.assertValueCount(1)
    }

    @Test
    fun testShowConfirmationAlert(){
        setup(environment()!!)
        vm.inputs.username("88554433")
        vm.inputs.password("123Zimplista_")
        vm.inputs.signInButtonPressed()
        val error = SignInError.userNotConfirmed
        val wrapper = ErrorWrapper(error,"El usuario no está confirmado")
        this.showError.assertValues(wrapper)
        this.showConfirmationAlert.assertValueCount(1)
    }

    @Test
    fun testLoadingEnabled(){
        setup(environment()!!)
        //loadingEnabled.assertValues(false)

        vm.inputs.username("88889999")
        vm.inputs.password("123Jose_")
        vm.inputs.signInButtonPressed()
        loadingEnabled.assertValues( true, false)
    }
}