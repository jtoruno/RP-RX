package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.FragmentViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface PayFragmentVM {
    interface Inputs {
        fun keyPressed(key : String)
    }
    interface Outputs{
        fun nextButtonEnabled() : Observable<Boolean>
        fun changeAmountAction() : Observable<String>
    }

    class ViewModel (@NonNull val environment: Environment) : FragmentViewModel<PayFragmentVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this
        var amountFloat : Float = 0.0F
        private val delete = "⬅"

        //Inputs
        private val keyPressed = PublishSubject.create<String>()

        //Outputs
        private val nextButtonEnabled = BehaviorSubject.create<Boolean>()
        private val changeAmountAction = PublishSubject.create<String>()

        init {
            keyPressed
                .map { newValue ->
                    return@map this.handleAmountChange(newValue)
                }
                .subscribe(this.changeAmountAction)

            keyPressed
                .map { return@map this.validAmount() }
                .subscribe(this.nextButtonEnabled)
        }


        override fun keyPressed(key: String) {
            return this.keyPressed.onNext(key)
        }

        override fun nextButtonEnabled(): Observable<Boolean> = this.nextButtonEnabled

        override fun changeAmountAction(): Observable<String> = this.changeAmountAction

        private fun handleAmountChange(newValue : String): String{
            val max = 999999.0F
            if (newValue == delete){
                if(amountFloat<10F){
                    refreshAmount()
                }else{
                    val toInt = amountFloat.toInt()
                    amountFloat = toInt.div(10).toFloat()
                }
            }
            else{
                if(amountFloat*10 <= max){
                    amountFloat *= 10
                    amountFloat += newValue.toInt()
                }
            }
            return formatFloatToString(amountFloat)
        }
        private fun validAmount() : Boolean {
            return amountFloat > 0.0F && amountFloat < 999999.0F
        }

        private fun refreshAmount() {
            amountFloat = 0.0F
        }
        private fun formatFloatToString(mFloat : Float): String{
            return "₡ "+String.format("%,.1f", mFloat)
        }

    }
}