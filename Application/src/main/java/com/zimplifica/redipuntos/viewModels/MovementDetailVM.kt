package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import android.util.Log
import com.zimplifica.domain.entities.Transaction
import com.zimplifica.domain.entities.TransactionStatus
import com.zimplifica.domain.entities.WayToPay
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface MovementDetailVM {
    interface Inputs {
        fun paymentInfoButtonPressed()
    }
    interface Outputs {
        fun paymentInfoButtonAction() : Observable<WayToPay>
        fun transactionAction() : Observable<Transaction>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<MovementDetailVM>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val paymentInfoButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val paymentInfoButtonAction = BehaviorSubject.create<WayToPay>()
        private val transactionObserver = BehaviorSubject.create<Transaction>()


        private val statusObservable : PublishSubject<Unit> = PublishSubject.create()

        init {
            val transactionIntent = intent()
                .filter { it.hasExtra("transaction") }
                .map { return@map it.getSerializableExtra("transaction") as Transaction }

            transactionIntent
                .subscribe(this.transactionObserver)

            paymentInfoButtonPressed
                .map { this.transactionObserver.value.wayToPay }
                .subscribe(this.paymentInfoButtonAction)

            /*
             val event = transactionIntent
                .filter { it.status == TransactionStatus.pending }
                 .flatMap { return@flatMap Observable.interval(2,TimeUnit.SECONDS,AndroidSchedulers.mainThread())
                     .takeUntil(statusObservable)
                     .flatMap { return@flatMap this.updateTransaction(transactionObserver.value.id) } }

            event
                .subscribe {trx ->
                    Log.e("status",trx.status.toString())
                    if (trx.status != TransactionStatus.pending){
                        this.statusObservable.onNext(Unit)
                        this.statusObservable.onComplete()
                        this.transactionObserver.onNext(trx)
                    }
                }
                */

        }

        override fun paymentInfoButtonPressed() {
            return this.paymentInfoButtonPressed.onNext(Unit)
        }

        override fun transactionAction(): Observable<Transaction> = this.transactionObserver

        override fun paymentInfoButtonAction(): Observable<WayToPay> = this.paymentInfoButtonAction

        private fun updateTransaction(id : String) : Observable<Transaction>{
            return Observable.create<Transaction> { disposable ->
                val transactionEvent = environment.userUseCase().getTransactionById(id)
                    .share()

                transactionEvent
                    .filter { !it.isFail() }
                    .map { it.successValue()!! }
                    .subscribe {trx ->
                        if(trx.status != TransactionStatus.pending){
                            environment.userUseCase().getUserInformation(false)
                                .subscribe{
                                    disposable.onNext(trx)
                                }
                        }else{
                            disposable.onNext(trx)
                        } } }
        }
    }
}