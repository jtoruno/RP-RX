package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Category
import com.zimplifica.domain.entities.Result
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface CategoriesVM {
    interface Inputs {
        // Call when is required to fetch the categories.
        fun fetchCategories()

        /// Call when user selects a category.
        fun categorySelected(category: Category)
    }
    interface Outputs {
        // Emits when gets the categories info.
        fun categoriesResult() : Observable<List<Category>>

        /// Emits when user selects a category.
        fun categorySelectedAction() : Observable<Category>

        /// Emits when loading changes its state.
        fun loadingActive() : Observable<Boolean>

        /// Emits when fetch categories has returned an error.
        fun showError() : Observable<String>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<CategoriesVM>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this
        private val resources = RPApplication.applicationContext().resources

        //Inputs
        private val fetchCategories = PublishSubject.create<Unit>()
        private val categorySelected = PublishSubject.create<Category>()

        //Outputs
        private val categoriesResult = BehaviorSubject.create<List<Category>>()
        private val categorySelectedAction = BehaviorSubject.create<Category>()
        private val loadingActive = BehaviorSubject.create<Boolean>()
        private val showError = BehaviorSubject.create<String>()

        init {
            val categoriesEvent = this.fetchCategories
                .flatMap { return@flatMap this.fetchCategoriesServer() }
                .share()

            categoriesEvent
                .filter { it.isFail() }
                .map { resources.getString(R.string.Error_fetch_commerces) }
                .subscribe(this.showError)
            categoriesEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.categoriesResult)

            this.categorySelected
                .subscribe(this.categorySelectedAction)
        }

        override fun fetchCategories() {
            return this.fetchCategories.onNext(Unit)
        }

        override fun categorySelected(category: Category) {
            return this.categorySelected.onNext(category)
        }

        override fun categoriesResult(): Observable<List<Category>> = this.categoriesResult

        override fun categorySelectedAction(): Observable<Category> = this.categorySelectedAction

        override fun loadingActive(): Observable<Boolean> = this.loadingActive

        override fun showError(): Observable<String> = this.showError

        private fun fetchCategoriesServer() : Observable<Result<List<Category>>>{
            return environment.userUseCase().fetchCategories()
                .doOnComplete { this.loadingActive.onNext(false) }
                .doOnSubscribe { this.loadingActive.onNext(true) }
        }

    }
}