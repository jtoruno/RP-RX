package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.FragmentViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface PointsFragmentVM {
    interface Inputs {
        fun fetchPaymentMethods()
        fun showDisablePaymentMethodAlert()
        fun disablePaymentMethodPressed(paymentMethod: PaymentMethod)
    }
    interface Outputs{
        fun paymentMethods() : Observable<List<PaymentMethod>>
        fun totalPoints() : Observable<Double>
        fun newData() : Observable<Unit>

        fun showDisablePaymentMethodAlertAction() : Observable<Unit>
        fun disablePaymentMethodAction() : Observable<PaymentMethod>
        fun loading() : Observable<Boolean>
        fun showError() : Observable<String>

    }
    class ViewModel(@NonNull val environment: Environment) : FragmentViewModel<PointsFragmentVM>(environment), Inputs, Outputs{



        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val fetchPaymentMethods = PublishSubject.create<Unit>()
        private val showDisablePaymentMethodAlert = PublishSubject.create<Unit>()
        private val disablePaymentMethodPressed = PublishSubject.create<PaymentMethod>()

        //Outputs
        private val paymentMethods = BehaviorSubject.create<List<PaymentMethod>>()
        private val totalPoints = BehaviorSubject.create<Double>()
        private val newData = BehaviorSubject.create<Unit>()
        private val showDisablePaymentMethodAlertAction = BehaviorSubject.create<Unit>()
        private val disablePaymentMethodAction = BehaviorSubject.create<PaymentMethod>()
        private val loading = BehaviorSubject.create<Boolean>()
        private val showError = BehaviorSubject.create<String>()


        init {
            val fetchPaymentMethodsEvent = Observable.fromArray(environment.currentUser().getCurrentUser())
                .takeWhen(this.fetchPaymentMethods)
                .flatMap {
                    val userInformation = it.second
                    if (userInformation==null){
                        val list = mutableListOf<PaymentMethod>()
                        val redCard = PaymentMethod("rediPuntos-card","", "",
                            "RediPuntos",userInformation?.rewards?:0.0,false)
                        list.add(redCard)
                        return@flatMap Observable.fromArray<Result<List<PaymentMethod>>>(Result.success(list))
                    }
                    else{
                        return@flatMap this.fetchPaymentMethods(userInformation)
                    }
                }
                .share()

            fetchPaymentMethodsEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.paymentMethods)

            environment.userUseCase().getUserInformationSubscription()
                .map { Unit }
                .subscribe(this.newData)

            showDisablePaymentMethodAlert
                .subscribe(this.showDisablePaymentMethodAlertAction)

            val disablePaymentMethodEvent = disablePaymentMethodPressed
                .flatMap { return@flatMap this.disablePaymentMethod(it) }
                .share()

            disablePaymentMethodEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.disablePaymentMethodAction)

            disablePaymentMethodEvent
                .filter { it.isFail() }
                .map { return@map "Ocurrió un error al eliminar el medio de pago. Por favor intente más tarde." }
                .subscribe(this.showError)
        }

        override fun fetchPaymentMethods() {
            return this.fetchPaymentMethods.onNext(Unit)
        }

        override fun showDisablePaymentMethodAlert() {
            return this.showDisablePaymentMethodAlert.onNext(Unit)
        }

        override fun disablePaymentMethodPressed(paymentMethod: PaymentMethod) {
            return this.disablePaymentMethodPressed.onNext(paymentMethod)
        }

        override fun showDisablePaymentMethodAlertAction(): Observable<Unit> = this.showDisablePaymentMethodAlertAction

        override fun disablePaymentMethodAction(): Observable<PaymentMethod> = this.disablePaymentMethodAction

        override fun loading(): Observable<Boolean> = this.loading


        override fun paymentMethods(): Observable<List<PaymentMethod>> = this.paymentMethods

        override fun totalPoints(): Observable<Double> = this.totalPoints

        override fun newData(): Observable<Unit> = newData

        override fun showError(): Observable<String> = this.showError

        private fun fetchPaymentMethods(userInformation : UserInformationResult) : Observable<Result<List<PaymentMethod>>>{
            val single = Single.create<Result<List<PaymentMethod>>> create@{ single ->
                val paymentArray = mutableListOf<PaymentMethod>()
                val paymentMethods = environment.currentUser().getCurrentUser()?.paymentMethods
                if(paymentMethods!=null){
                    paymentArray.addAll(paymentMethods)
                }
                paymentArray.add(PaymentMethod("rediPuntos-card","", "",
                    "RediPuntos",userInformation.rewards?:0.0,false))
                single.onSuccess(Result.success(paymentArray.toList()))

            }
            return single.toObservable()
        }

        private fun disablePaymentMethod(paymentMethod: PaymentMethod) : Observable<Result<PaymentMethod>>{
            val userInfo = environment.currentUser().getCurrentUser()
            return environment.userUseCase().disablePaymentMethod(userInfo?.userId ?: "",paymentMethod.cardId)
                .doOnComplete { this.loading.onNext(false) }
                .doOnSubscribe { this.loading.onNext(true) }
        }

    }
}