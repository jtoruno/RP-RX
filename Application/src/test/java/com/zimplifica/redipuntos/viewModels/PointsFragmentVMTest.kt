package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class PointsFragmentVMTest : RPTestCase() {
    lateinit var vm : PointsFragmentVM.ViewModel
    val paymentMethods = TestObserver<List<PaymentMethod>>()

    private fun setUpEnvironment(environment: Environment){
        vm = PointsFragmentVM.ViewModel(environment)
        vm.outputs.paymentMethods().subscribe(this.paymentMethods)
        vm.outputs.paymentMethods().subscribe{
            if(it.isNotEmpty()){
                println(it[0].cardNumberWithMask)
            }
        }

    }

    @Test
    fun testPaymentMethods(){
        val paymentList = mutableListOf<PaymentMethod>()
        paymentList.add(PaymentMethod("1",  "1234", "", "visa", 3000.0, false))
        val currentUser = UserInformationResult("550e8400-e29b-41d4-a716-446655440000", "11565O433",
            "José", "Sanchez",
            "10/10/1994", "josedani.04.24@gmail.com", "+50686137284",
            true,  false,null, 1000.0, paymentList)
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
        vm.inputs.fetchPaymentMethods()
        paymentMethods.assertValueCount(1)
    }
}