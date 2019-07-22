package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Commerce
import com.zimplifica.domain.entities.Promotion
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface CommercePromotionsVM {
    interface Inputs {
        /// Call when user selects a promotion.
        fun promotionSelected(promotion: Promotion)
    }
    interface Outputs{
        /// Emits when gets the commerce info.
        fun commerceResult() : Observable<Commerce>

        /// Emits when user selects a promotion.
        fun promotionSelectedAction() : Observable<Promotion>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<CommercePromotionsVM.ViewModel>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val promotionSelected = PublishSubject.create<Promotion>()

        //Outputs
        private val commerceResult = BehaviorSubject.create<Commerce>()
        private val promotionSelectedAction = BehaviorSubject.create<Promotion>()

        init {
            intent()
                .filter { it.hasExtra("commerce") }
                .map { it.getSerializableExtra("commerce") as Commerce }
                .subscribe(this.commerceResult)

            this.promotionSelected
                .subscribe(this.promotionSelectedAction)
        }

        override fun promotionSelected(promotion: Promotion) {
            return this.promotionSelected.onNext(promotion)
        }

        override fun commerceResult(): Observable<Commerce> = this.commerceResult

        override fun promotionSelectedAction(): Observable<Promotion> = this.promotionSelectedAction
    }
}