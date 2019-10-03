package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.UserStatus
import com.zimplifica.domain.entities.VerificationStatus
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.mocks.userInformationMock
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class HomeViewModelTest : RPTestCase() {
    lateinit var vm : HomeViewModel.ViewModel
    val signOutAction = TestObserver<Unit>()
    val showCompletePersonalInfoAlert = TestObserver<VerificationStatus>()
    val goToCompletePersonalInfoScreen = TestObserver<Unit>()


    private fun setUpEnvironment(environment: Environment){
        this.vm = HomeViewModel.ViewModel(environment)
        this.vm.outputs.signOutAction().subscribe(this.signOutAction)
        this.vm.outputs.showCompletePersonalInfoAlert().subscribe(this.showCompletePersonalInfoAlert)
        this.vm.outputs.goToCompletePersonalInfoScreen().subscribe(this.goToCompletePersonalInfoScreen)
    }

    @Test
    fun testSignOutAction(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.signOutButtonPressed()
        this.signOutAction.assertValueCount(1)
    }

    @Test
    fun testGoToCompletePersonalInfoScreen(){
        setUpEnvironment(environment()!!)
        this.vm.inputs.completePersonalInfoButtonPressed()
        this.goToCompletePersonalInfoScreen.assertValueCount(1)
    }

}