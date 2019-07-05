package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.Category
import com.zimplifica.domain.entities.Commerce
import com.zimplifica.domain.entities.CommercesResult
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.FragmentViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface CommercesFragmentVM {
    interface Inputs {
        /// Call when is required to fetch the Commerces.
        fun fetchCommerces()
        /// Call when user selects a Commerce.
        fun commerceSelected(commerce: Commerce)
        /// Call when the filter button is pressed.
        fun filterButtonPressed()
        /// Call when the filter returns a category.
        fun filterByCategory(category: Category)

        /// Call when the user selects a category.
        fun categorySelected(category: Category)
        /// Call when the search button is pressed.
        fun searchButtonPressed(searchText: String)
    }
    interface Outputs {
        /// Emits when gets the commerces info.
        fun commerces() : Observable<CommercesResult>
        /// Emits when fetch Commerces has returned an error.
        fun showError() : Observable<String>
        /// Emits when user selects a Commerce.
        fun commerceSelectedAction() : Observable<Commerce>

        /// Emits when loading changes its state.
        fun loadingActive() : Observable<Boolean>

        /// Emits when the filter button is pressed.
        fun filterButtonAction() : Observable<Unit>

        /// Emits when the user selects a category.
        fun categorySelectedAction() : Observable<Category>
    }
    class ViewModel (@NonNull val environment: Environment) : FragmentViewModel<CommercesFragmentVM>(environment),Inputs,Outputs{


        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val fetchCommerces = PublishSubject.create<Unit>()
        private val commerceSelected = PublishSubject.create<Commerce>()
        private val filterButtonPressed = PublishSubject.create<Unit>()
        private val filterByCategory = PublishSubject.create<Category>()
        private val categorySelected = PublishSubject.create<Category>()
        private val searchButtonPressed = PublishSubject.create<String>()

        //Outputs
        private val commerces = BehaviorSubject.create<CommercesResult>()
        private val showError = BehaviorSubject.create<String>()
        private val commerceSelectedAction = BehaviorSubject.create<Commerce>()
        private val loadingActive = BehaviorSubject.create<Boolean>()
        private val filterButtonAction = BehaviorSubject.create<Unit>()
        private val categorySelectedAction = BehaviorSubject.create<Category>()

        init {
            this.commerceSelected
                .subscribe(this.commerceSelectedAction)
            this.filterButtonPressed
                .subscribe(this.filterButtonAction)
            this.categorySelected
                .subscribe(this.categorySelectedAction)

            val commerceEvent = this.fetchCommerces
                .flatMap { return@flatMap this.fetchCommercesResult(null,null) }
                .share()

            val searchEvent = this.searchButtonPressed
                .flatMap { return@flatMap this.searchCommerces(it) }
                .share()

            val categoryFilterEvent = this.filterByCategory
                .flatMap { return@flatMap this.filterCommercesByCategory(it) }
                .share()

            Observable.merge(commerceEvent,searchEvent,categoryFilterEvent)
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.commerces)

            Observable.merge(commerceEvent,searchEvent,categoryFilterEvent)
                .filter { it.isFail() }
                .map { return@map "Error al obetener comercios." }
                .subscribe(this.showError)
        }

        override fun fetchCommerces() {
            return this.fetchCommerces.onNext(Unit)
        }

        override fun commerceSelected(commerce: Commerce) {
            return this.commerceSelected.onNext(commerce)
        }

        override fun filterButtonPressed() {
            return this.filterButtonPressed.onNext(Unit)
        }

        override fun filterByCategory(category: Category) {
            return this.filterByCategory.onNext(category)
        }

        override fun categorySelected(category: Category) {
            return this.categorySelected.onNext(category)
        }

        override fun searchButtonPressed(searchText: String) {
            return this.searchButtonPressed.onNext(searchText)
        }

        override fun filterButtonAction(): Observable<Unit> = this.filterButtonAction

        override fun categorySelectedAction(): Observable<Category> = this.categorySelectedAction

        override fun commerces(): Observable<CommercesResult> = this.commerces

        override fun showError(): Observable<String> = this.showError

        override fun commerceSelectedAction(): Observable<Commerce> = this.commerceSelectedAction

        override fun loadingActive(): Observable<Boolean> = this.loadingActive

        private fun fetchCommercesResult(limit: Int?, nextToken: String?) : Observable<Result<CommercesResult>>{
           return environment.userUseCase().getCommerces(limit,nextToken)
               .doOnComplete { this.loadingActive.onNext(false) }
               .doOnSubscribe { this.loadingActive.onNext(true) }
        }

        private fun searchCommerces(searchText: String) : Observable<Result<CommercesResult>>{
            return environment.userUseCase().searchCommerces(searchText)
                .doOnComplete { this.loadingActive.onNext(false) }
                .doOnSubscribe { this.loadingActive.onNext(true) }
        }

        private fun filterCommercesByCategory(category : Category) : Observable<Result<CommercesResult>>{
            return environment.userUseCase().filterCommercesByCategory(category.id)
                .doOnComplete { this.loadingActive.onNext(false) }
                .doOnSubscribe { this.loadingActive.onNext(true) }
        }
    }
}