package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import android.util.Log
import com.zimplifica.domain.entities.PaymentPayload
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.Vendor
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CheckAndPayModel
import com.zimplifica.redipuntos.models.SitePaySellerSelectionObject
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.lang.Exception
import java.time.temporal.TemporalAmount

interface SPScanQRVM {
    interface Inputs {
        fun codeFound(code : String)
    }
    interface Outputs{
        fun showError() : Observable<String>
        //fun getVendorInformationAction() : Observable<Vendor>
        fun nextScreenAction() : Observable<CheckAndPayModel>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<SPScanQRVM>(environment),Inputs,Outputs{



        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val codeFound = PublishSubject.create<String>()
        //Outputs
        private val showError = BehaviorSubject.create<String>()
        //private val getVendorInformationAction = BehaviorSubject.create<Vendor>()

        private val nextScreenAction = BehaviorSubject.create<CheckAndPayModel>()

        init {
            val amountObservable = intent()
                .filter { it.hasExtra("amount") }
                .map {
                    return@map it.getFloatExtra("amount", 0F)
                }

            val getVendorInfoEvent = this.codeFound
                //.skipWhile { it == "" }
                .flatMap {
                    Log.e("VMQR",it)
                    return@flatMap this.getVendorInformation(it)
                }
                .share()

            getVendorInfoEvent
                .filter { it.isFail() }
                .map { return@map "Código inválido, intente de nuevo." }
                .subscribe(this.showError)

            val vendorInfo = getVendorInfoEvent
                .filter { !it.isFail() }
                .map{return@map it.successValue()!!}


            val form = Observables.combineLatest(vendorInfo,amountObservable)

            val paymentEvent = form
                .flatMap { return@flatMap this.requestPayment(it.first?.pk?:"",it.second) }
                .share()

            paymentEvent
                .filter { it.isFail() }
                .map { return@map "Ha ocurrido un error al realizar el pago." }
                .subscribe(this.showError)

            val paymentPayloadResult = paymentEvent
                .filter { !it.isFail() }
                .map { it.successValue()!! }

            Observables.combineLatest(paymentPayloadResult,vendorInfo)
                .map {
                    val vendor = it.second
                    val payload = it.first
                    return@map CheckAndPayModel(payload.order.pid,payload.order.subtotal,payload.order.fee,payload.order.tax,payload.order.total,payload.rediPuntos,payload.order.cashback,payload.order.taxes,payload.paymentMethods, vendor)
                }
                .subscribe(this.nextScreenAction)
        }


        override fun codeFound(code: String) {
            return this.codeFound.onNext(code)
        }

        override fun showError(): Observable<String> = this.showError

        override fun nextScreenAction(): Observable<CheckAndPayModel> = this.nextScreenAction

        //override fun getVendorInformationAction(): Observable<Vendor> = this.getVendorInformationAction

        private fun getVendorInformation (vendorId: String) : Observable<Result<Vendor>>{
            return environment.userUseCase().getVendorInformation(vendorId)
        }

        private fun requestPayment(vendorId: String, amount: Float) : Observable<Result<PaymentPayload>>{
            return environment.userUseCase().checkoutPayloadSitePay("",amount,vendorId,"")
        }
    }

}