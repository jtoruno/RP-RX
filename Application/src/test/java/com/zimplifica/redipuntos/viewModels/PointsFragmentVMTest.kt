package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.UserStatus
import com.zimplifica.domain.entities.VerificationStatus
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CurrentUser
import io.reactivex.observers.TestObserver
import org.junit.Test

class PointsFragmentVMTest : RPTestCase() {
    lateinit var vm : PointsFragmentVM.ViewModel
    val paymentMethods = TestObserver<List<PaymentMethod>>()
    val showDisablePaymentMethodAlertAction = TestObserver<Unit>()
    val disablePaymentMethodAction = TestObserver<PaymentMethod>()
    val loading = TestObserver<Boolean>()
    val showError = TestObserver<String>()

    private fun setUpEnvironment(environment: Environment){
        vm = PointsFragmentVM.ViewModel(environment)
        vm.outputs.paymentMethods().subscribe(this.paymentMethods)
        vm.outputs.paymentMethods().subscribe{
            if(it.isNotEmpty()){
                println(it[0].cardNumberWithMask)
            }
        }
        vm.outputs.loading().subscribe(this.loading)
        vm.outputs.disablePaymentMethodAction().subscribe(this.disablePaymentMethodAction)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.showDisablePaymentMethodAlertAction().subscribe(this.showDisablePaymentMethodAlertAction)


    }

    @Test
    fun testPaymentMethods(){
        val status = UserStatus(VerificationStatus.VerifiedValid,null)
        val paymentList = mutableListOf<PaymentMethod>()
        paymentList.add(PaymentMethod("1",  "1234", "", "visa", 3000.0, false))
        val currentUser = UserInformationResult("550e8400-e29b-41d4-a716-446655440000", "11565O433",
            "José", "Sanchez",
            "10/10/1994", "josedani.04.24@gmail.com", "+50686137284",
            true,  false,null, 1000.0, paymentList,status)
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
        vm.inputs.fetchPaymentMethods()
        paymentMethods.assertValueCount(1)
    }

    @Test
    fun testShowDisablePaymentMethodAlertAction(){
        val status = UserStatus(VerificationStatus.VerifiedValid,null)
        val paymentList = mutableListOf<PaymentMethod>()
        paymentList.add(PaymentMethod("1",  "1234", "", "visa", 3000.0, false))
        val currentUser = UserInformationResult("550e8400-e29b-41d4-a716-446655440000", "11565O433",
            "José", "Sanchez",
            "10/10/1994", "josedani.04.24@gmail.com", "+50686137284",
            true,  false,null, 1000.0, paymentList,status)
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
        vm.inputs.showDisablePaymentMethodAlert()
        this.showDisablePaymentMethodAlertAction.assertValueCount(1)
    }

    @Test
    fun testDisablePaymentMethodAction(){
        val status = UserStatus(VerificationStatus.VerifiedValid,null)
        val paymentList = mutableListOf<PaymentMethod>()
        paymentList.add(PaymentMethod("1234",  "1234", "", "visa", 3000.0, false))
        val currentUser = UserInformationResult("1234", "11565O433",
            "José", "Sanchez",
            "10/10/1994", "josedani.04.24@gmail.com", "+50686137284",
            true,  false,null, 1000.0, paymentList,status)
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
        val paymentMethod = PaymentMethod("1234",  "1234",  "", "visa", 4505.0, false)
        vm.inputs.disablePaymentMethodPressed(paymentMethod)
        disablePaymentMethodAction.assertValueCount(1)

    }

    @Test
    fun testShowError(){
        val status = UserStatus(VerificationStatus.VerifiedValid,null)
        val paymentList = mutableListOf<PaymentMethod>()
        paymentList.add(PaymentMethod("1234",  "1234", "", "visa", 3000.0, false))
        val currentUser = UserInformationResult("1234", "11565O433",
            "José", "Sanchez",
            "10/10/1994", "josedani.04.24@gmail.com", "+50686137284",
            true,  false,null, 1000.0, paymentList,status)
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
        val paymentMethod = PaymentMethod("3333",  "1234",  "", "visa", 4505.0, false)
        vm.inputs.disablePaymentMethodPressed(paymentMethod)
        disablePaymentMethodAction.assertValueCount(0)
        showError.assertValueCount(1)
        showError.assertValue("Ocurrió un error al eliminar el medio de pago. Por favor intente más tarde.")

    }

    @Test
    fun testLoading(){
        val status = UserStatus(VerificationStatus.VerifiedValid,null)
        val paymentList = mutableListOf<PaymentMethod>()
        paymentList.add(PaymentMethod("1234",  "1234", "", "visa", 3000.0, false))
        val currentUser = UserInformationResult("1234", "11565O433",
            "José", "Sanchez",
            "10/10/1994", "josedani.04.24@gmail.com", "+50686137284",
            true,  false,null, 1000.0, paymentList,status)
        val currentU = CurrentUser
        currentU.setCurrentUser((currentUser))
        setUpEnvironment(environment()!!.toBuilder().currentUser(currentU).build())
        val paymentMethod = PaymentMethod("3333",  "1234",  "", "visa", 4505.0, false)
        vm.inputs.disablePaymentMethodPressed(paymentMethod)
        loading.assertValues(true,false)
    }
}
