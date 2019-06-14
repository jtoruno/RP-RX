package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import android.util.Log
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.Vendor
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface SPScanQRVM {
    interface Inputs {
        fun codeFound(code : String)
    }
    interface Outputs{
        fun showError() : Observable<String>
        fun getVendorInformationAction() : Observable<Vendor>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<SPScanQRVM>(environment),Inputs,Outputs{


        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val codeFound = PublishSubject.create<String>()
        //Outputs
        private val showError = BehaviorSubject.create<String>()
        private val getVendorInformationAction = BehaviorSubject.create<Vendor>()

        init {
            val getVendorInfoEvent = this.codeFound
                //.skipWhile { it == "" }
                .flatMap {
                    Log.e("VMQR",it)
                    return@flatMap this.getVendorInformation(it)
                }
                .share()

            getVendorInfoEvent
                .filter { !it.isFail() }
                .map{return@map it.successValue()}
                .subscribe(this.getVendorInformationAction)

            getVendorInfoEvent
                .filter { it.isFail() }
                .map { return@map "Código inválido, intente de nuevo." }
                .subscribe(this.showError)


        }


        override fun codeFound(code: String) {
            return this.codeFound.onNext(code)
        }

        override fun showError(): Observable<String> = this.showError

        override fun getVendorInformationAction(): Observable<Vendor> = this.getVendorInformationAction

        private fun getVendorInformation (vendorId: String) : Observable<Result<Vendor>>{
            return environment.userUseCase().getVendorInformation(vendorId)
        }

    }
}