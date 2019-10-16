package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ValidationService
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface PromoCodeVM {
    interface Inputs {
        /// Call when apply button is pressed.
        fun applyButtonPressed(promoCode: String)

        /// Call when string value of promo code textfield is changed.
        fun promoCodeChanged(promoCode: String)
    }
    interface Outputs {
        /// Emits when apply button is pressed.
        fun applyAction() : Observable<Unit>

        /// Emits when apply button is enabled.
        fun applyButtonEnabled() : Observable<Boolean>

        /// Emits when apply promo code is loading.
        fun loading() : Observable<Boolean>

    }
    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<PromoCodeVM>(environment),Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val applyButtonPressed = PublishSubject.create<String>()
        private val promoCodeChanged = PublishSubject.create<String>()

        //Outputs
        private val applyAction = BehaviorSubject.create<Unit>()
        private val applyButtonEnabled = BehaviorSubject.create<Boolean>()
        private val loading = BehaviorSubject.create<Boolean>()

        init {
            this.applyButtonPressed
                .subscribe {
                    this.applyPromoCode()
                    this.applyAction.onNext(Unit)
                }

            this.promoCodeChanged
                .map { return@map ValidationService.validatePromotionCode(it) }
                .subscribe(applyButtonEnabled)
        }


        override fun applyButtonPressed(promoCode: String) {
            return this.applyButtonPressed.onNext(promoCode)
        }

        override fun promoCodeChanged(promoCode: String) {
            return  this.promoCodeChanged.onNext(promoCode)
        }

        override fun applyAction(): Observable<Unit> = this.applyAction

        override fun applyButtonEnabled(): Observable<Boolean> = this.applyButtonEnabled

        override fun loading(): Observable<Boolean> = this.loading


        private fun applyPromoCode(){
            loading.onNext(true)
            loading.onNext(false)
        }
    }
}