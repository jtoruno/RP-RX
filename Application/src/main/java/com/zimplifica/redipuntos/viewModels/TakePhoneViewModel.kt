package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.redipuntos.extensions.takeWhen
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ValidationService
import com.zimplifica.redipuntos.models.SignUpUsernameModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface TakePhoneViewModel {
    interface Inputs {
        fun phone(phone: String)
        fun nicknameChanged(username: String)
        fun nextButtonClicked()

    }
    interface Outputs {
        fun nextButtonIsEnabled() : Observable<Boolean>
        fun startNextActivity(): Observable<SignUpUsernameModel>
    }

    class ViewModel(@NonNull val environment: Environment):ActivityViewModel<TakePhoneViewModel>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val phoneEditTextChanged = PublishSubject.create<String>()
        private val nextButtonClicked = PublishSubject.create<Unit>()
        private val nicknameChanged = PublishSubject.create<String>()

        //Outputs
        private val nextButtonIsEnabled = BehaviorSubject.create<Boolean>()
        private val startNextActivity = BehaviorSubject.create<SignUpUsernameModel>()

        init {

            val formData = Observables.combineLatest(nicknameChanged,
                phoneEditTextChanged)

            formData
                .map { ValidationService.validateNicknameAndUsername(it.first,it.second) }
                .subscribe(this.nextButtonIsEnabled)

            formData
                .takeWhen(this.nextButtonClicked)
                .filter { ValidationService.validateNicknameAndUsername(it.second.first,it.second.second) }
                .map {
                    val username = it.second.second
                    val nickname = it.second.first
                    return@map SignUpUsernameModel(username,nickname)
                }
                .subscribe(this.startNextActivity)
        }

        override fun phone(phone: String) = this.phoneEditTextChanged.onNext(phone)

        override fun nextButtonClicked() = this.nextButtonClicked.onNext(Unit)

        override fun nicknameChanged(username: String) {
            return this.nicknameChanged.onNext(username)
        }

        override fun nextButtonIsEnabled(): Observable<Boolean> = this.nextButtonIsEnabled

        override fun startNextActivity(): Observable<SignUpUsernameModel> = this.startNextActivity

    }
}