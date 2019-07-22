package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.Transaction
import com.zimplifica.domain.entities.TransactionsResult
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.FragmentViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface MovementsFragmentVM {
    interface Inputs {
        fun fetchTransactions(useCache : Boolean)
        fun showMovementDetail(transaction: Transaction)
    }
    interface Outputs {
        fun showMovementDetailAction() : Observable<Transaction>
        fun fetchTransactionsAction() : Observable<List<Pair<String,List<Transaction>>>>

        /// Emits when getTransactions has returned an error.
        fun showError() : Observable<String>
    }
    class ViewModel (@NonNull val environment: Environment) : FragmentViewModel<MovementsFragmentVM>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val fetchTransactions = PublishSubject.create<Boolean>()
        private val showMovementDetail = PublishSubject.create<Transaction>()

        //Outputs
        private val showMovementDetailAction = BehaviorSubject.create<Transaction>()
        private val fetchTransactionsAction = BehaviorSubject.create<List<Pair<String,List<Transaction>>>>()
        private val showError = BehaviorSubject.create<String>()

        init {
            val fetchTransactionsEvent =  fetchTransactions
                .flatMap { return@flatMap this.fetchTransactionsServer(it) }
                .share()

            fetchTransactionsEvent
                .filter { it.isFail() }
                .map { "Ocurrió un error al obtener los movimientos. Por favor intenta de nuevo." }
                .subscribe(this.showError)

            fetchTransactionsEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .map { this.handleTransactionsData(it) }
                .subscribe(this.fetchTransactionsAction)

            this.showMovementDetail
                .subscribe(this.showMovementDetailAction)
        }

        override fun fetchTransactions(useCache: Boolean) {
            return this.fetchTransactions.onNext(useCache)
        }

        override fun showMovementDetail(transaction: Transaction) {
            return this.showMovementDetail.onNext(transaction)
        }

        override fun showMovementDetailAction(): Observable<Transaction> = this.showMovementDetailAction

        override fun fetchTransactionsAction(): Observable<List<Pair<String, List<Transaction>>>> = this.fetchTransactionsAction

        override fun showError(): Observable<String> = this.showError

        private fun fetchTransactionsServer(useCache: Boolean) : Observable<Result<TransactionsResult>>{
            return environment.userUseCase().fetchTransactions(environment.currentUser().getCurrentUser()?.userId?:"",useCache)
        }

        private fun handleTransactionsData(transactionsResult: TransactionsResult) : List<Pair<String,List<Transaction>>>{
            val dateSections = mutableListOf<String>()
            transactionsResult.transactions.map { trx ->
                if(!dateSections.contains(trx.date)){
                    dateSections.add(trx.date)
                }
            }

            return dateSections.map { date ->
                val transactions = transactionsResult.transactions.filter { it.date == date }
                return@map Pair(date,transactions)
            }
        }

    }
}