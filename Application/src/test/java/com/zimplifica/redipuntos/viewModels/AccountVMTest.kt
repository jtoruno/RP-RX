package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.PinRequestMode
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.mocks.userInformationMock
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class AccountVMTest : RPTestCase() {
    lateinit var vm : AccountVM.ViewModel
    private val completeAccountInfoAction = TestObserver<UserInformationResult>()
    private val updateUserInfo = TestObserver<UserInformationResult>()
    private val signOutAction = TestObserver<Unit>()
    private val termsAndConditionsButton = TestObserver<Unit>()
    private val privacyPolicyButton = TestObserver<Unit>()
    private val aboutUsButton = TestObserver<Unit>()

    private val goToChangePasswordScreenAction = TestObserver<Unit>()
    private val changePasswordButtonAction =  TestObserver<Unit>()
    private val pinButtonAction =  TestObserver<PinRequestMode>()
    private val showUpdatePinAlert =  TestObserver<Unit>()
    private val biometricAuthEnabled = TestObserver<Unit>()
    private var verifyPinSecurityCode = TestObserver<Unit>()
    private val referFriendButtonAction = TestObserver<Unit>()
    private val promoCodeButtonAction = TestObserver<Unit>()
    private val enablePremiumAction = TestObserver<Unit>()

    private fun setUpEnvironment(environment: Environment){

        vm = AccountVM.ViewModel(environment)
        vm.outputs.completeAccountInfoAction().subscribe(this.completeAccountInfoAction)
        vm.outputs.updateUserInfo().subscribe(this.updateUserInfo)
        vm.outputs.signOutAction().subscribe(this.signOutAction)
        vm.outputs.termsAndConditionsButton().subscribe(this.termsAndConditionsButton)
        vm.outputs.privacyPolicyButton().subscribe(this.privacyPolicyButton)
        vm.outputs.aboutUsButton().subscribe(this.aboutUsButton)

        vm.outputs.goToChangePasswordScreenAction().subscribe(this.goToChangePasswordScreenAction)
        vm.outputs.changePasswordButtonAction().subscribe(this.goToChangePasswordScreenAction)
        vm.outputs.changePasswordButtonAction().subscribe(this.changePasswordButtonAction)
        vm.outputs.pinButtonAction().subscribe(this.pinButtonAction)
        vm.outputs.showUpdatePinAlert().subscribe(this.showUpdatePinAlert)
        vm.outputs.biometricAuthEnabled().subscribe(this.biometricAuthEnabled)
        vm.outputs.verifyPinSecurityCode().subscribe(this.verifyPinSecurityCode)
        vm.outputs.referFriendButtonAction().subscribe(this.referFriendButtonAction)
        vm.outputs.promoCodeButtonAction().subscribe(this.promoCodeButtonAction)
        vm.outputs.enablePremiumAction().subscribe(this.enablePremiumAction)
    }

    @Test
    fun testSignOutAction(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.signOutButtonPressed()
        signOutAction.assertValueCount(1)
    }

    @Test
    fun testTermsAndConditions(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.termsAndConditionsButtonPressed()
        termsAndConditionsButton.assertValueCount(1)
    }

    @Test
    fun testPrivacyPolicyButton(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.privacyPolicyButtonPressed()
        privacyPolicyButton.assertValueCount(1)
    }

    @Test
    fun testAboutUs(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.aboutUsButtonPressed()
        aboutUsButton.assertValueCount(1)
    }

    @Test
    fun testChangePasswordAction(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.goToChangePasswordScreen()
        goToChangePasswordScreenAction.assertValueCount(1)
    }

    @Test
    fun testShowUpdatePinAlert(){
        setUpEnvironment(true)
        vm.inputs.onCreate()
        vm.inputs.pinButtonPressed()
        showUpdatePinAlert.assertValueCount(1)
    }

    @Test
    fun testShowCreatePinScreen(){
        setUpEnvironment(false)
        vm.inputs.onCreate()
        vm.inputs.pinButtonPressed()
        pinButtonAction.assertValue(PinRequestMode.CREATE)
    }

    @Test
    fun testPinButtonAction(){
        setUpEnvironment(true)
        vm.inputs.onCreate()
        vm.inputs.showUpdatePinScreen()
        pinButtonAction.assertValue(PinRequestMode.UPDATE)
    }

    @Test
    fun testChangeBiometricAuth(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        biometricAuthEnabled.assertValueCount(1)
        vm.inputs.biometricAuthChanged(true)
        biometricAuthEnabled.assertValueCount(2)
    }

    @Test
    fun testVerifyPinSecurityCode(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.biometricAuthChangeAccepted(true)
        verifyPinSecurityCode.assertValueCount(1)
    }

    @Test
    fun testPinSecurityCodeStatusAction(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.pinSecurityCodeStatusAction.onNext(Unit)
        biometricAuthEnabled.assertValueCount(2)
    }

    @Test
    fun testReferFriendButtonAction(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.referFriendButtonPressed()
        referFriendButtonAction.assertValueCount(1)
    }

    @Test
    fun testPromoCodeButtonAction(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.promoCodeButtonPressed()
        promoCodeButtonAction.assertValueCount(1)
    }

    @Test
    fun testEnablePremiumAction(){
        setUpEnvironment(environment()!!)
        vm.inputs.onCreate()
        vm.inputs.enablePremiumButtonPressed()
        enablePremiumAction.assertValueCount(1)
    }

    private fun setUpEnvironment(verifiedStatus : Boolean){
        val userInformation = userInformationMock(userEmailVerified = verifiedStatus,securityCodeCreated = verifiedStatus)
        val environment = environment()!!
        environment.currentUser().setCurrentUser(userInformation)
        setUpEnvironment(environment)

    }
}