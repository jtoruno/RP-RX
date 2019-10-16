package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.*
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.mocks.userInformationMock
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class HomeViewModelTest : RPTestCase() {
    lateinit var vm : HomeViewModel.ViewModel
    private val signOutAction = TestObserver<Unit>()
    private val showCompletePersonalInfoAlert = TestObserver<VerificationStatus>()
    private val goToCompletePersonalInfoScreen = TestObserver<Unit>()
    private val showAlert = TestObserver<Pair<String,String>>()
    private val showRateCommerceAlert = TestObserver<RateCommerceModel>()


    private fun setUpEnvironment(environment: Environment){
        this.vm = HomeViewModel.ViewModel(environment)
        this.vm.outputs.signOutAction().subscribe(this.signOutAction)
        this.vm.outputs.showCompletePersonalInfoAlert().subscribe(this.showCompletePersonalInfoAlert)
        this.vm.outputs.goToCompletePersonalInfoScreen().subscribe(this.goToCompletePersonalInfoScreen)
        this.vm.outputs.showAlert().subscribe(this.showAlert)
        this.vm.outputs.showRateCommerceAlert().subscribe(this.showRateCommerceAlert)
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

    @Test
    fun testAlertServerEvent(){
        setUpEnvironment(environment()!!)
        val serverEvent = ServerEvent( "1234", "",  "Alert",  "1",  "Test",  "Test Message",  "",  false,  false,  false)
        environment()!!.userUseCase().state.registerActionableEvent(serverEvent)
        showAlert.assertValueCount(1)
    }

}