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
        fun transactionUpdated() : Observable<Transaction>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<MovementDetailVM>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val paymentInfoButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val paymentInfoButtonAction = BehaviorSubject.create<WayToPay>()
        private val transactionUpdated = BehaviorSubject.create<Transaction>()


        init {
            val transactionIntent = intent()
                .filter { it.hasExtra("transaction") }
                .map { return@map it.getSerializableExtra("transaction") as Transaction }

            transactionIntent
                .subscribe(this.transactionUpdated)

            paymentInfoButtonPressed
                .map { this.transactionUpdated.value.wayToPay }
                .subscribe(this.paymentInfoButtonAction)

            environment.userUseCase().getPaymentsSubscription()
                .filter { it.id == transactionUpdated.value.id }
                .subscribe(this.transactionUpdated)

            transactionIntent
                .filter { it.status == TransactionStatus.pending }
                .flatMap { return@flatMap environment.userUseCase().getTransactionById(it.id) }
                .filter { !it.isFail() }
                .map { return@map it.successValue() }
                .subscribe(this.transactionUpdated)


        }

        override fun paymentInfoButtonPressed() {
            return this.paymentInfoButtonPressed.onNext(Unit)
        }

        override fun transactionUpdated(): Observable<Transaction> = this.transactionUpdated

        override fun paymentInfoButtonAction(): Observable<WayToPay> = this.paymentInfoButtonAction
    }
}