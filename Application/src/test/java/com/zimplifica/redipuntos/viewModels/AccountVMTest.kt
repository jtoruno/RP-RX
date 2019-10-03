package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
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

    private fun setUpEnvironment(environment: Environment){
        vm = AccountVM.ViewModel(environment)
        vm.outputs.completeAccountInfoAction().subscribe(this.completeAccountInfoAction)
        vm.outputs.updateUserInfo().subscribe(this.updateUserInfo)
        vm.outputs.signOutAction().subscribe(this.signOutAction)
        vm.outputs.termsAndConditionsButton().subscribe(this.termsAndConditionsButton)
        vm.outputs.privacyPolicyButton().subscribe(this.privacyPolicyButton)
        vm.outputs.aboutUsButton().subscribe(this.aboutUsButton)
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
}