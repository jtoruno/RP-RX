package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface HomeViewModel {
    interface Inputs {
        fun signOutButtonPressed()
    }
    interface Outputs {
        fun signOutAction() : Observable<Unit>

    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<HomeViewModel>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this
        var amountFloat : Float = 0.0F
        private val delete = "⬅"

        //Inputs
        private val signOutButtonPressed = PublishSubject.create<Unit>()


        //Outputs
        private val signOutAction = PublishSubject.create<Unit>()

        init {
            val signOutEvent = signOutButtonPressed
                .flatMap { this.signOut() }
                .share()
            signOutEvent
                .map {  return@map  }
                .subscribe(this.signOutAction)

        }

        override fun signOutButtonPressed() {
            return this.signOutButtonPressed.onNext(Unit)
        }

        override fun signOutAction(): Observable<Unit> = this.signOutAction

        private fun signOut() : Observable<Result<UserStateResult>>{
            return environment.authenticationUseCase().signOut()
        }

        private fun formatFloatToString(mFloat : Float): String{
            return "₡"+String.format("%,.1f", mFloat)
        }
    }
}