package com.zimplifica.redipuntos.viewModels

import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class ReferFriendVMTest : RPTestCase() {
    lateinit var vm : ReferFriendVM.ViewModel
    private val shareButtonAction = TestObserver<String>()
    private val copyToClipboardAction = TestObserver<String>()

    private fun setUpEnv(environment: Environment){
        vm = ReferFriendVM.ViewModel(environment)
        vm.outputs.copyToClipboardAction().subscribe(copyToClipboardAction)
        vm.outputs.shareButtonAction().subscribe(shareButtonAction)
    }

    @Test
    fun testShareButtonAction(){
        setUpEnv(environment()!!)
        vm.inputs.shareButtonPressed()
        shareButtonAction.assertValueCount(1)
    }

    @Test
    fun testCopyToClipboardAction(){
        setUpEnv(environment()!!)
        vm.inputs.copyToClipboardPressed()
        copyToClipboardAction.assertValueCount(1)
    }

}