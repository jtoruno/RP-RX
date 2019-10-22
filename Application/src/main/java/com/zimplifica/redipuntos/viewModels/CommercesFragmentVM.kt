package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import android.util.Log
import com.zimplifica.domain.entities.Category
import com.zimplifica.domain.entities.Commerce
import com.zimplifica.domain.entities.CommercesResult
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.FragmentViewModel
import com.zimplifica.redipuntos.models.FavoriteMerchant
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
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

        fun favoriteMerchantPressed(state: FavoriteMerchant)
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
        private val resources = RPApplication.applicationContext().resources

        //Inputs
        private val fetchCommerces = PublishSubject.create<Unit>()
        private val commerceSelected = PublishSubject.create<Commerce>()
        private val filterButtonPressed = PublishSubject.create<Unit>()
        private val filterByCategory = BehaviorSubject.create<Category>()
        private val categorySelected = PublishSubject.create<Category>()
        private val searchButtonPressed = BehaviorSubject.create<String>()
        private val favoriteMerchantPressed = PublishSubject.create<FavoriteMerchant>()

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
                .flatMap {
                    return@flatMap this.fetchCommercesResult(null,null)
                }
                .share()

            /*
            val form = Observables.zip(this.searchButtonPressed,this.filterByCategory)

            val event = form
                .flatMap {
                    Log.e("SearchEvent",it.first + "Text")
                    Log.e("SearchEvent",it.second.name + "Category")
                    return@flatMap this.generalSearch(null,null,it.first,it.second)
                }.share()*/


            val searchEvent = this.searchButtonPressed
                .flatMap {
                    Log.e("SearchEvent", "Category" + this.filterByCategory.value )
                    if(it.isEmpty()){
                        return@flatMap this.generalSearch(null,null,null,this.filterByCategory.value)
                    }else{
                        return@flatMap this.generalSearch(null,null,it,this.filterByCategory.value)
                    }

                }
                .share()

            val categoryFilterEvent = this.filterByCategory
                .flatMap {
                    Log.e("SearchEvent",this.searchButtonPressed.value + "search")
                    if(it.id == ""){
                        return@flatMap this.generalSearch(null,null,this.searchButtonPressed.value,null)
                    }else{
                        return@flatMap this.generalSearch(null,null,this.searchButtonPressed.value,it)
                    }
                }
                .share()

            Observable.merge(commerceEvent,searchEvent,categoryFilterEvent)
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.commerces)

            Observable.merge(commerceEvent,searchEvent,categoryFilterEvent)
                .filter { it.isFail() }
                .map { return@map resources.getString(R.string.Error_fetch_commerces) }
                .subscribe(this.showError)

            val favoriteEvent = favoriteMerchantPressed
                .flatMap {
                    this.updateFavoriteMerchant(it.merchantId,it.isFavorite)
                }
                .share()

            favoriteEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .map { Unit }
                .subscribe(this.fetchCommerces)
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

        override fun favoriteMerchantPressed(state: FavoriteMerchant) {
            return this.favoriteMerchantPressed.onNext(state)
        }

        override fun filterButtonAction(): Observable<Unit> = this.filterButtonAction

        override fun categorySelectedAction(): Observable<Category> = this.categorySelectedAction

        override fun commerces(): Observable<CommercesResult> = this.commerces

        override fun showError(): Observable<String> = this.showError

        override fun commerceSelectedAction(): Observable<Commerce> = this.commerceSelectedAction

        override fun loadingActive(): Observable<Boolean> = this.loadingActive

        private fun fetchCommercesResult(limit: Int?, skip: Int?) : Observable<Result<CommercesResult>>{
           return environment.userUseCase().getCommerces(limit,skip,null,null)
               .doOnComplete { this.loadingActive.onNext(false) }
               .doOnSubscribe { this.loadingActive.onNext(true) }
        }

        private fun updateFavoriteMerchant(merchantId: String, isFavorite: Boolean) : Observable<Result<Boolean>> {
            return environment.userUseCase().updateFavoriteMerchant(merchantId, isFavorite)
        }

        private fun generalSearch(limit: Int?, skip: Int?,searchText: String?,category : Category?) : Observable<Result<CommercesResult>>{
            return environment.userUseCase().getCommerces(limit,skip,category?.id,searchText)
                .doOnComplete { this.loadingActive.onNext(false) }
                .doOnSubscribe { this.loadingActive.onNext(true) }
        }
    }
}