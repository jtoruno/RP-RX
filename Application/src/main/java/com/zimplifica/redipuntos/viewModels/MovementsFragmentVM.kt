package com.zimplifica.redipuntos.viewModels

import android.util.Log
import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.Transaction
import com.zimplifica.domain.entities.TransactionsResult
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.FragmentViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject

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
        fun transactionList() : Observable<List<Transaction>>
    }
    class ViewModel (@NonNull val environment: Environment) : FragmentViewModel<MovementsFragmentVM>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Oagination Variables

        val PAGE_START = 1
        var currentPage = PAGE_START
        var isLastPage = false
        var totalPage = 10
        var isLoading = false
        var itemCount = 0
        var token : String ? = null

        //Inputs
        private val fetchTransactions = PublishSubject.create<Boolean>()
        private val showMovementDetail = PublishSubject.create<Transaction>()

        //Outputs
        private val showMovementDetailAction = BehaviorSubject.create<Transaction>()
        private val fetchTransactionsAction = BehaviorSubject.create<List<Pair<String,List<Transaction>>>>()
        private val showError = BehaviorSubject.create<String>()

        private val transactions = BehaviorSubject.create<List<Transaction>>()

        init {
            val fetchTransactionsEvent =  fetchTransactions
                .flatMap { return@flatMap this.fetchTransactionsServer(it, null, null) }
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

        override fun transactionList(): Observable<List<Transaction>> = this.transactions


        private fun fetchTransactionsServer(useCache: Boolean, nextToken: String?, limit: Int? ) : Observable<Result<TransactionsResult>>{
            return environment.userUseCase().fetchTransactions(useCache, nextToken, limit)
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

        fun preparedItems(useCache : Boolean){
            Log.e("Prepareditems",isLastPage.toString())
            if(!isLastPage){
                val transactionEvent = fetchTransactionsServer(useCache,token,7)
                transactionEvent
                    .filter { it.isFail() }
                    .map { "Ocurrió un error al obtener los movimientos. Por favor intenta de nuevo." }
                    .subscribe(this.showError)
                transactionEvent
                    .filter { !it.isFail() }
                    .map { it.successValue() }
                    .map {
                        itemCount += it.transactions.size
                        token = it.nextToken
                        return@map it.transactions
                    }
                    .subscribe{
                        this.transactions.onNext(it)
                    }
            }

        }
        fun onRefresh(){
            itemCount = 0
            currentPage = PAGE_START
            isLastPage = false
            token = null
            //Clean adapter
            //call get transactions
            Log.e("onRefresh",isLastPage.toString())
            preparedItems(false)
        }

    }
}