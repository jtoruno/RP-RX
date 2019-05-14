package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import android.util.Log
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface SplashViewModel {
    interface Inputs {
        fun onCreate()
    }
    interface Outputs{
        fun splashAction() : Observable<UserStateResult>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<SplashViewModel>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()

        //Outputs
        private val splashAction = BehaviorSubject.create<UserStateResult>()

        init {

            val getSession = onCreate
                .flatMap { this.submit() }
                .share()
            //val getSession = this.submit()

            //getSession.subscribe { Log.e("Result",it.isFail().toString() + (it.successValue() as UserStateResult).toString()) }

            getSession
                .map {
                    when(it){
                        is Result.success -> return@map it.value as UserStateResult
                        is Result.failure -> return@map null
                    }
                }
                .subscribe(this.splashAction)
        }

        override fun onCreate() {
            this.onCreate.onNext(Unit)
        }

        override fun splashAction(): Observable<UserStateResult> {
            return this.splashAction
        }
        private fun submit() : Observable<Result<UserStateResult>>{
            return environment.authenticationUseCase().getCurrentUserState()
        }

    }
}