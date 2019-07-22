package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface LaunchViewModel {
    interface Inputs{
        fun onCreate()
    }
    interface Outputs{
        fun nextScreen() : Observable<UserInformationResult>

    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<LaunchViewModel>(environment), Inputs,Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()

        //Outputs
        private val nextScreen = BehaviorSubject.create<UserInformationResult>()

        init {
            val userInfo = onCreate
                .flatMap { this.loadingUserInfo() }
                .share()
            userInfo
                .filter { !it.isFail() }
                .map {
                    return@map it.successValue()
                }
                .subscribe(this.nextScreen)
        }

        override fun onCreate() {
             this.onCreate.onNext(Unit)
        }

        override fun nextScreen(): Observable<UserInformationResult> = this.nextScreen

        private fun loadingUserInfo():Observable<Result<UserInformationResult>>{
            return environment.userUseCase().getUserInformation(false)
        }
    }
}