package com.zimplifica.redipuntos.viewModels

import android.content.Context
import android.support.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface SignInViewModel {
    interface Inputs {
        fun username(username: String)
        fun password(password: String)

    }
    interface Outputs {
        fun signInButtonIsEnabled() : Observable<Boolean>
    }

    class ViewModel(@NonNull val context: Context) : ActivityViewModel<SignInViewModel>(context), Inputs, Outputs{
        //Inputs
        private val usernameEditTextChanged =  PublishSubject.create<String>()
        private val passwordEditTextChanged =  PublishSubject.create<String>()


        //Outputs
        private val signInButtonIsEnabled = BehaviorSubject.create<Boolean>()

        val inputs : Inputs = this
        val outputs : Outputs = this

        init {
            val usernameAndPassword = combineLatest<String, String, Pair<String, String>>(usernameEditTextChanged,passwordEditTextChanged,
                BiFunction { t1, t2 ->
                    Pair(t1, t2)
                })
            val valid = usernameAndPassword.map {
                isValid(it.first,it.second)
            }
            valid.subscribe(this.signInButtonIsEnabled)

        }
        override fun username(username: String) = this.usernameEditTextChanged.onNext(username)

        override fun password(password: String) = this.passwordEditTextChanged.onNext(password)

        override fun signInButtonIsEnabled(): Observable<Boolean> = this.signInButtonIsEnabled

        private  fun isValid (username: String, password: String) = username.isNotEmpty() && password.isNotEmpty()

    }
}