package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface FirstVM {
    interface Inputs {
        fun onCreate()
    }
    interface Outputs {
        fun signedInAction(): Observable<Unit>
        fun signedOutAction(): Observable<Unit>
    }

    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<FirstVM.ViewModel>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        private val onCreate = PublishSubject.create<Unit>()

        private val signedInAction = BehaviorSubject.create<Unit>()
        private val signedOutAction = BehaviorSubject.create<Unit>()

        init {
            val session = onCreate
                .flatMap { environment.authenticationUseCase().getCurrentUserState() }
                .share()
            session
                .filter { it == UserStateResult.signedOut }
                .map { Unit }
                .subscribe(this.signedOutAction)

            session
                .filter {  it == UserStateResult.signedIn }
                .map { Unit }
                .subscribe(this.signedInAction)
        }

        override fun onCreate() {
            return this.onCreate.onNext(Unit)
        }

        override fun signedInAction(): Observable<Unit> = this.signedInAction

        override fun signedOutAction(): Observable<Unit> = this.signedOutAction

    }
}