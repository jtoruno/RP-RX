package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.ServerEvent
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.observers.TestObserver
import org.junit.Test

class NotificationsVMTest : RPTestCase() {
    lateinit var vm : NotificationsVM.ViewModel
    private val updateNotifications = TestObserver<List<ServerEvent>>()

    private fun setUpEnv(environment: Environment){
        vm = NotificationsVM.ViewModel(environment)
        vm.outputs.updateNotifications().subscribe(updateNotifications)
    }

    @Test
    fun testUpdateNotifications(){
        setUpEnv(environment()!!)
        vm.inputs.onCreate()
        updateNotifications.assertValueCount(1)
    }

    @Test
    fun testSwipeToUpdate(){
        setUpEnv(environment()!!)
        vm.inputs.swiped()
        updateNotifications.assertValueCount(1)
    }
}