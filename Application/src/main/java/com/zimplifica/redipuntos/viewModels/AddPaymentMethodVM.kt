package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.domain.entities.PaymentMethodInput
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CreditCardExpirationDate
import com.zimplifica.redipuntos.models.CreditCardNumber
import com.zimplifica.redipuntos.models.CreditCardSegurityCode
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface AddPaymentMethodVM{
    interface Inputs{
        fun cardHolderChanged(cardHolder: String)
        ///  Call when the card number changed.

        fun cardNumberChanged(number: String)
        /// Call when the expiration date changed.

        fun cardExpirationChanged(date: String)
        /// Call when the security code changed.

        fun cardSecurityCodeChanged(code: String)

        /// Call when the add payment method button is pressed.
        fun addPaymentMethodButtonPressed()
    }
    interface Outputs {
        fun cardHolder(): Observable<String>

        // Emits a card number object when the card number changed.
        fun cardNumber(): Observable<CreditCardNumber>

        // Emits a expiration date obj when the expiration date changed.
        fun cardExpirationDate(): Observable<CreditCardExpirationDate>

        // Emits a security code object when the security code changed.
        fun cardSecurityCode(): Observable<CreditCardSegurityCode>

        // Emits a boolean when the form is completed.
        fun isFormValid(): Observable<Boolean>

        // Emits and Alert error to be displayed.
        fun showError(): Observable<String>

        // Emits a boolean when add payment method operation is being processed or not.
        fun loading(): Observable<Boolean>

        // Emits when a payment method is saved and should transition to the next view.
        fun paymentMethodSaved(): Observable<PaymentMethod>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<AddPaymentMethodVM>(environment),Inputs,Outputs{
        val inputs : Inputs = this
        val outputs : Outputs = this
        private val resources = RPApplication.applicationContext().resources

        //Inputs
        private val cardHolderChanged = PublishSubject.create<String>()
        private val cardNumberChanged = PublishSubject.create<String>()
        private val cardExpirationChanged = PublishSubject.create<String>()
        private val cardSecurityCodeChanged = PublishSubject.create<String>()
        private val addPaymentMethodButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val cardHolder = BehaviorSubject.create<String>()
        private val cardNumber = BehaviorSubject.create<CreditCardNumber>()
        private val cardExpirationDate = BehaviorSubject.create<CreditCardExpirationDate>()
        private val cardSecurityCode = BehaviorSubject.create<CreditCardSegurityCode>()
        //private val cardSecurityCode : Observable<CreditCardSegurityCode>
        private val isFormValid = BehaviorSubject.create<Boolean>()
        private val showError = BehaviorSubject.create<String>()
        private val loading = BehaviorSubject.create<Boolean>()
        private val paymentMethodSaved = BehaviorSubject.create<PaymentMethod>()


        init {
            cardHolderChanged
                .subscribe(this.cardHolder)

            cardNumberChanged
                .map { CreditCardNumber(it) }
                .share()
                .subscribe(this.cardNumber)

            cardExpirationChanged
                .map { CreditCardExpirationDate(it) }
                .share()
                .subscribe(this.cardExpirationDate)

            Observable.combineLatest<CreditCardNumber, String, Pair<CreditCardNumber, String>>(
                cardNumber,
                cardSecurityCodeChanged,
                BiFunction { t1, t2 ->
                    Pair(t1, t2)
                }
            ).map { CreditCardSegurityCode(it.second,it.first.issuer) }
                .share()
                .subscribe(this.cardSecurityCode)

            /*
            val form1 = Observable.combineLatest<String, CreditCardNumber, Pair<String, CreditCardNumber>>(
                cardHolder, cardNumber,
                BiFunction { t1, t2 ->
                    Pair(t1, t2)
                }
            )*/

            val form : Observable<Boolean> = Observables.combineLatest(cardHolder,cardNumber,
                cardExpirationDate,cardSecurityCode) {a,b,c,d -> return@combineLatest (a.isNotEmpty()
                    && b.status == CreditCardNumber.Status.valid
                    && c.isValid == CreditCardExpirationDate.ExpirationDateStatus.valid
                    && d.isValid)}

            form.subscribe(this.isFormValid)

            /*
            form1
                .map {
                    return@map (it.first.isNotEmpty()
                            && it.second.status == CreditCardNumber.Status.valid
                            && cardExpirationDate.value.isValid == CreditCardExpirationDate.ExpirationDateStatus.valid
                            && cardSecurityCode.value.isValid)
                }
                .subscribe(this.isFormValid)*/

            val addPaymentMethodEvent = form
                .takeWhen(addPaymentMethodButtonPressed)
                .filter{
                    it.second
                }
                .flatMap {
                    val cardHolder = cardHolder.value
                    val cardNumber = cardNumber.value.value
                    val expirationDate = cardExpirationDate.value.fullDate
                    val securityCode = cardSecurityCode.value.valueFormatted
                    return@flatMap this.addPaymentMethod(cardHolder,cardNumber,expirationDate?:"",securityCode)
                }.share()
            addPaymentMethodEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.paymentMethodSaved)

            addPaymentMethodEvent
                .filter { it.isFail() }
                .map { return@map resources.getString(R.string.Error_add_payment_method) }
                .subscribe(this.showError)

        }

        override fun cardHolderChanged(cardHolder: String) = this.cardHolderChanged.onNext(cardHolder)

        override fun cardNumberChanged(number: String) {
            return this.cardNumberChanged.onNext(number)
        }

        override fun cardExpirationChanged(date: String) = this.cardExpirationChanged.onNext(date)

        override fun cardSecurityCodeChanged(code: String) = this.cardSecurityCodeChanged.onNext(code)

        override fun addPaymentMethodButtonPressed(){
            return this.addPaymentMethodButtonPressed.onNext(Unit)
        }

        override fun cardHolder(): Observable<String> = this.cardHolder

        override fun cardNumber(): Observable<CreditCardNumber> = this.cardNumber

        override fun cardExpirationDate(): Observable<CreditCardExpirationDate> = this.cardExpirationDate

        override fun cardSecurityCode(): Observable<CreditCardSegurityCode> = this.cardSecurityCode

        override fun isFormValid(): Observable<Boolean> = this.isFormValid

        override fun showError(): Observable<String> = this.showError

        override fun loading(): Observable<Boolean> = this.loading

        override fun paymentMethodSaved(): Observable<PaymentMethod> = this.paymentMethodSaved

        private fun addPaymentMethod(holderName: String, cardNumber: String, expirationDate: String, securityCode: String): Observable<Result<PaymentMethod>>{
            val input = PaymentMethodInput(cardNumber,holderName,expirationDate,securityCode)
            return environment.userUseCase().addPaymentMethod(input)
                .doOnComplete { this.loading.onNext(false) }
                .doOnSubscribe { this.loading.onNext(true) }
        }
    }
}