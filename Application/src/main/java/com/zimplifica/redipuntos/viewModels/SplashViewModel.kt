package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import android.util.Log
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
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
        //fun splashAction() : Observable<UserStateResult>
        fun signedInAction(): Observable<Unit>
        fun signedOutAction(): Observable<Unit>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<SplashViewModel>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()

        //Outputs
        private val signedInAction = BehaviorSubject.create<Unit>()
        private val signedOutAction = BehaviorSubject.create<Unit>()

        init {

            val getSession = onCreate
                .flatMap { this.submit() }
                .share()
            //val getSession = this.submit()

            //getSession.subscribe { Log.e("Result",it.isFail().toString() + (it.successValue() as UserStateResult).toString()) }

            getSession
                .filter { it == UserStateResult.signedOut }
                .map { it -> Unit }
                .subscribe(this.signedOutAction)

            val signedIn = getSession
                .filter { it == UserStateResult.signedIn }
                .flatMap { this.finishLoadingUserInfo() }
                .share()

            signedIn
                .filter { !it.isFail() }
                .map {
                    return@map it.successValue()
                }
                .map {
                    Log.i("UserInfo","nnnn"+it.userPhoneNumber)
                    return@map Unit
                }
                .subscribe(this.signedInAction)

        }

        override fun onCreate() {
            this.onCreate.onNext(Unit)
        }

        override fun signedInAction(): Observable<Unit> = this.signedInAction

        override fun signedOutAction(): Observable<Unit> = this.signedOutAction
        /*
        override fun splashAction(): Observable<UserStateResult> {
            return this.splashAction
        }*/
        private fun submit() : Observable<UserStateResult>{
            Log.e("Print",environment.currentUser().getCurrentUser().toString())
            return environment.authenticationUseCase().getCurrentUserState()
        }

        private fun finishLoadingUserInfo() : Observable<Result<UserInformationResult>>{
            return environment.userUseCase().getUserInformation(false)
        }

    }
}