package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.Transaction
import com.zimplifica.domain.entities.WayToPay
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

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
        private val transactionobserver = BehaviorSubject.create<Transaction>()

        init {
            val transactionIntent = intent()
                .filter { it.hasExtra("transaction") }
                .map { return@map it.getSerializableExtra("transaction") as Transaction }

            transactionIntent
                .subscribe(this.transactionobserver)

            paymentInfoButtonPressed
                .map { this.transactionobserver.value.wayToPay }
                .subscribe(this.paymentInfoButtonAction)
        }

        override fun paymentInfoButtonPressed() {
            return this.paymentInfoButtonPressed.onNext(Unit)
        }

        override fun transactionAction(): Observable<Transaction> = this.transactionobserver

        override fun paymentInfoButtonAction(): Observable<WayToPay> = this.paymentInfoButtonAction

    }
}