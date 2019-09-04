package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.domain.entities.UserStatus
import com.zimplifica.domain.entities.VerificationStatus
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class SplashViewModelTest : RPTestCase(){
    lateinit var vm : SplashViewModel.ViewModel
    val finishLoadingUserInfo = TestObserver<UserInformationResult>()
    val didFinishWithError = TestObserver<String>()
    val backToWelcome = TestObserver<Unit>()
    val retryLoading = TestObserver<Boolean>()


    private fun setUpEnvironment(environment: Environment){
        this.vm = SplashViewModel.ViewModel(environment)
        this.vm.outputs.finishLoadingUserInfo().subscribe(this.finishLoadingUserInfo)
        this.vm.outputs.didFinishWithError().subscribe(this.didFinishWithError)
        this.vm.outputs.backToWelcome().subscribe(this.backToWelcome)
        this.vm.outputs.retryLoading().subscribe(this.retryLoading)
    }

    @Test
    fun testFinishLoadingUserInfo(){
        val status = UserStatus( VerificationStatus.Pending,null)

        //val userInfo = UserInformationResult("e5a06e84-73f4-4a04-bcbc-a70552a4d92a", "115650044","PEDRO", "FONSECA ARGUEDAS", "10/10/1993", "pedro@redi.com", "+50699443322", true,  true, null,  0.0, mutableListOf(), status)

        setUpEnvironment(environment()!!)
        this.vm.inputs.onCreate()
        this.vm.inputs.finishAnimation()
        finishLoadingUserInfo.assertValueCount(1)
    }

    @Test
    fun testBackToWelcome(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.backButtonPressed()
        backToWelcome.assertValueCount(1)
    }

    @Test
    fun testLoading(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.finishAnimation()
        retryLoading.assertValues(true,false)
    }
}