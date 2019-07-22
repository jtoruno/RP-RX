package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface TakePhoneViewModel {
    interface Inputs {
        fun phone(phone: String)
        fun nextButtonClicked()

    }
    interface Outputs {
        fun nextButtonIsEnabled() : Observable<Boolean>
        fun startNextActivity(): Observable<Unit>
    }

    class ViewModel(@NonNull val environment: Environment):ActivityViewModel<TakePhoneViewModel>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val phoneEditTextChanged = PublishSubject.create<String>()
        private val nextButtonClicked = PublishSubject.create<Unit>()

        //Outputs
        private val nextButtonIsEnabled = BehaviorSubject.create<Boolean>()
        private val startNextActivity : Observable<Unit>

        init {
            this.startNextActivity = this.nextButtonClicked

            val valid = phoneEditTextChanged
                .map { isValid(it) }
            valid.subscribe(nextButtonIsEnabled)
        }

        override fun phone(phone: String) = this.phoneEditTextChanged.onNext(phone)

        override fun nextButtonClicked() = this.nextButtonClicked.onNext(Unit)

        override fun nextButtonIsEnabled(): Observable<Boolean> = this.nextButtonIsEnabled

        override fun startNextActivity(): Observable<Unit> = this.startNextActivity

        private fun isValid(phone: String): Boolean{
            val allowedPhoneNumbers = mutableListOf("4","5","6","7","8","9")
            return if(phone.length!=8){
                false
            }else{
                val phoneStart = phone.substring(0,1)
                allowedPhoneNumbers.contains(phoneStart)
            }

        }

    }
}