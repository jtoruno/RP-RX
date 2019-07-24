package com.zimplifica.redipuntos.viewModels

import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.redipuntos.RPTestCase
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CreditCardExpirationDate
import com.zimplifica.redipuntos.models.CreditCardNumber
import com.zimplifica.redipuntos.models.CreditCardSegurityCode
import io.reactivex.observers.TestObserver
import org.junit.Test

class AddPaymentVMTest : RPTestCase() {
    lateinit var vm : AddPaymentVM.ViewModel
    val cardHolder = TestObserver<String>()
    val cardNumber = TestObserver<CreditCardNumber>()
    val cardExpirationDate = TestObserver<CreditCardExpirationDate>()
    val cardSecurityCode = TestObserver<CreditCardSegurityCode>()
    val isFormValid = TestObserver<Boolean>()
    val paymentMethodSaved = TestObserver<PaymentMethod>()
    val showError = TestObserver<String>()
    val loading = TestObserver<Boolean>()

    private fun setUpEnvironment(environment: Environment){
        vm = AddPaymentVM.ViewModel(environment)
        vm.outputs.cardHolder().subscribe(this.cardHolder)
        vm.outputs.cardNumber().subscribe(this.cardNumber)
        vm.outputs.cardExpirationDate().subscribe(this.cardExpirationDate)
        vm.outputs.cardSecurityCode().subscribe(this.cardSecurityCode)
        vm.outputs.isFormValid().subscribe(this.isFormValid)
        vm.outputs.paymentMethodSaved().subscribe(this.paymentMethodSaved)
        vm.outputs.showError().subscribe(this.showError)
        vm.outputs.loading().subscribe(this.loading)
        vm.outputs.cardNumber().subscribe {
            print(it.issuer.name + it.status.name +it.valueFormatted )
        }
    }

    @Test
    fun testCardHolder(){
        setUpEnvironment(environment()!!)
        vm.inputs.cardHolderChanged("JOSE FRANCISCO PORRAS")
        cardHolder.assertValues("JOSE FRANCISCO PORRAS")
    }

    @Test
    fun testCardNumber(){
        var value = "34"
        var cardNumberObj = CreditCardNumber(value)
        assertEquals(cardNumberObj.valueFormatted,"34")
        assertEquals(cardNumberObj.issuer, CreditCardNumber.Issuer.AMEX)
        assertEquals(cardNumberObj.status, CreditCardNumber.Status.unkown)

        value = "4539169425022428"
        cardNumberObj = CreditCardNumber(value)
        assertEquals(cardNumberObj.valueFormatted,"4539 1694 2502 2428")
        assertEquals(cardNumberObj.issuer, CreditCardNumber.Issuer.VISA)
        assertEquals(cardNumberObj.status, CreditCardNumber.Status.valid)

        value = "5432392899889241"
        cardNumberObj = CreditCardNumber(value)
        assertEquals(cardNumberObj.valueFormatted,"5432 3928 9988 9241")
        assertEquals(cardNumberObj.issuer, CreditCardNumber.Issuer.MASTERCARD)
        assertEquals(cardNumberObj.status, CreditCardNumber.Status.invalid)

        setUpEnvironment(environment()!!)
        vm.inputs.cardNumberChanged(value)
        cardNumber.assertValueCount(1)
    }

    @Test
    fun testExpirationDate(){
        var value = "10/1"
        var expirationDate = CreditCardExpirationDate(value)
        assertEquals(expirationDate.isValid, CreditCardExpirationDate.ExpirationDateStatus.unkown)

        value = "10/10"
        expirationDate = CreditCardExpirationDate(value)
        assertEquals(expirationDate.isValid,CreditCardExpirationDate.ExpirationDateStatus.invalid)

        value = "10/20"
        expirationDate = CreditCardExpirationDate(value)
        assertEquals(expirationDate.isValid, CreditCardExpirationDate.ExpirationDateStatus.valid)
        assertEquals(expirationDate.valueFormatted, "10/20")
        assertEquals(expirationDate.fullDate, "01/10/2020")

        setUpEnvironment(environment()!!)
        vm.inputs.cardExpirationChanged(value)
        cardExpirationDate.assertValueCount(1)
    }

    @Test
    fun testSecurityCode(){
        var issuer = CreditCardNumber.Issuer.AMEX
        var value = "10"
        var code = CreditCardSegurityCode(value,issuer)
        assertEquals(code.isValid, false)


        value = "1014"
        code = CreditCardSegurityCode(value,issuer)
        assertEquals(code.isValid, true)

        issuer = CreditCardNumber.Issuer.MASTERCARD
        value = "105"
        code = CreditCardSegurityCode(value,  issuer)
        assertEquals(code.isValid, true)

        setUpEnvironment(environment()!!)
        vm.inputs.cardNumberChanged("5432392899889241")
        vm.inputs.cardSecurityCodeChanged( value)
        cardSecurityCode.assertValueCount(1)

    }

    @Test
    fun testIsFormValid(){
        setUpEnvironment(environment()!!)
        vm.inputs.cardHolderChanged("JOSE PABLO")
        vm.inputs.cardNumberChanged("5432392899889241")
        vm.inputs.cardSecurityCodeChanged( "30")
        vm.inputs.cardExpirationChanged( "10/22")
        isFormValid.assertValues(false)
        vm.inputs.cardSecurityCodeChanged("301")
        isFormValid.assertValues(false, false)
        vm.inputs.cardNumberChanged("4539169425022428")
        isFormValid.assertValues(false, false, false,true)

        vm.inputs.cardNumberChanged("45391694250224")
        isFormValid.assertValues(false, false, false, true, true, false)
    }

    @Test
    fun testPaymentMethodSaved(){
        setUpEnvironment(environment()!!)
        vm.inputs.cardHolderChanged("JOSE PABLO")
        vm.inputs.cardExpirationChanged( "10/22")
        vm.inputs.cardSecurityCodeChanged("301")
        vm.inputs.cardNumberChanged("4539169425022428")
        vm.inputs.addPaymentMethodButtonPressed()
        paymentMethodSaved.assertValueCount(1)
    }

    @Test
    fun testShowError(){
        setUpEnvironment(environment()!!)
        vm.inputs.cardHolderChanged("JOSE PABLO")
        vm.inputs.cardExpirationChanged("10/22")
        vm.inputs.cardSecurityCodeChanged("301")
        vm.inputs.cardNumberChanged("5297896540294552")
        vm.inputs.addPaymentMethodButtonPressed()
        showError.assertValueCount(1)
    }

    @Test
    fun testLoading(){
        setUpEnvironment(environment()!!)
        vm.inputs.cardHolderChanged("JOSE PABLO")
        vm.inputs.cardExpirationChanged("10/22")
        vm.inputs.cardSecurityCodeChanged("301")
        vm.inputs.cardNumberChanged("5297896540294552")
        vm.inputs.addPaymentMethodButtonPressed()
        loading.assertValues(true,false)

    }


}