package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.PaymentPayload
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.Vendor
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface SPSelectionVM {
    interface Inputs {
        fun qrCameraButtonPressed()
        fun nextButtonPressed()
        fun descriptionTextFieldChanged(description: String)
        fun vendorInformation(vendor: Vendor)
    }

    interface Outputs{
        fun qrCameraButtonAction() : Observable<Unit>
        fun nextButtonAction() : Observable<SitePaySellerSelectionObject>
        fun nextButtonEnabled() : Observable<Boolean>
        fun nextButtonLoadingIndicator() : Observable<Boolean>
        fun showError() : Observable<String>
        fun descriptionTextField() : Observable<String>
        fun vendorInformationAction() : Observable<Vendor>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<SPSelectionVM>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        private var amount = 0F
        //Inputs
        private val qrCameraButtonPressed = PublishSubject.create<Unit>()
        private val nextButtonPressed = PublishSubject.create<Unit>()
        private val descriptionTextFieldChanged = BehaviorSubject.create<String>()
        private val vendorInformation = PublishSubject.create<Vendor>()

        //Outputs
        private val qrCameraButtonAction = BehaviorSubject.create<Unit>()
        private val nextButtonAction = BehaviorSubject.create<SitePaySellerSelectionObject>()
        private val nextButtonEnabled = BehaviorSubject.create<Boolean>()
        private val nextButtonLoadingIndicator = BehaviorSubject.create<Boolean>()
        private val showError = BehaviorSubject.create<String>()
        private val descriptionTextField = BehaviorSubject.create<String>()
        private val vendorInformationAction = BehaviorSubject.create<Vendor>()

        init {


            vendorInformation
                .subscribe(this.vendorInformationAction)
            qrCameraButtonPressed
                .subscribe(this.qrCameraButtonAction)
            descriptionTextFieldChanged
                .subscribe(this.descriptionTextField)

            val amountObservable = intent()
                .filter { it.hasExtra("amount") }
                .map {
                    this.amount = it.getFloatExtra("amount", 0F)
                    println("Amount"+amount)
                    return@map it.getFloatExtra("amount", 0F)
                }
            /*
            vendorInformation
                .map { return@map this.validInputs(it.name) }
                .subscribe(this.nextButtonEnabled)*/

            val valid = Observable.combineLatest<Vendor, String, Pair<Vendor, String>>(this.vendorInformation,descriptionTextFieldChanged,
                BiFunction { t1, t2 ->
                    Pair(t1, t2)
                })
            valid.map { return@map it.first.name.isNotEmpty() && it.second.isNotEmpty() }
                .subscribe(this.nextButtonEnabled)

            val formData = Observable.combineLatest<Vendor,Float,Pair<Vendor,Float>>(this.vendorInformation,amountObservable,
                BiFunction { t1, t2 ->
                    Pair(t1, t2)
                })


            val checkoutPayloadEvent = formData
                .takeWhen(nextButtonPressed)
                .flatMap { return@flatMap this.checkoutPayloadSitePay(it.second.first.pk,this.descriptionTextFieldChanged.value, it.second.second) }
                .share()

            checkoutPayloadEvent
                .filter { it.isFail() }
                .map { return@map "Ocurrió un error inesperado, por favor intenta de nuevo." }
                .subscribe(this.showError)

            val paymentPayloadResult = checkoutPayloadEvent
                .filter { !it.isFail() }
                .map { it.successValue() }

            Observables.combineLatest(paymentPayloadResult,this.vendorInformationAction)
                .map { return@map SitePaySellerSelectionObject(it.second,it.first!!) }
                .subscribe(nextButtonAction)





        }

        override fun qrCameraButtonPressed() {
           return this.qrCameraButtonPressed.onNext(Unit)
        }

        override fun nextButtonPressed() {
            return this.nextButtonPressed.onNext(Unit)
        }

        override fun descriptionTextFieldChanged(description: String) {
            return this.descriptionTextFieldChanged.onNext(description)
        }

        override fun vendorInformation(vendor: Vendor) {
            return this.vendorInformation.onNext(vendor)
        }

        override fun qrCameraButtonAction(): Observable<Unit> = this.qrCameraButtonAction

        override fun nextButtonAction(): Observable<SitePaySellerSelectionObject> = this.nextButtonAction

        override fun nextButtonEnabled(): Observable<Boolean> = this.nextButtonEnabled

        override fun nextButtonLoadingIndicator(): Observable<Boolean> = this.nextButtonLoadingIndicator

        override fun showError(): Observable<String> = this.showError

        override fun descriptionTextField(): Observable<String> = this.descriptionTextField

        override fun vendorInformationAction(): Observable<Vendor> = this.vendorInformationAction

        private fun checkoutPayloadSitePay(vendorId : String, description : String, amount : Float) : Observable<Result<PaymentPayload>>{
            return environment.userUseCase().checkoutPayloadSitePay(amount,vendorId)
                .doOnComplete { this.nextButtonLoadingIndicator.onNext(false) }
                .doOnSubscribe { this.nextButtonLoadingIndicator.onNext(true) }
        }

        private fun validInputs(vendorId: String) : Boolean{
            return vendorId.isNotEmpty()
        }

        fun getAmount(): Float{
            return this.amount
        }

    }
}