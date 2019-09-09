package com.zimplifica.redipuntos.viewModels

import android.annotation.SuppressLint
import androidx.annotation.NonNull
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface SplashViewModel {
    interface Inputs {
        fun onCreate()
        fun finishAnimation()
        fun retryButtonPressed()
        fun backButtonPressed()
    }
    interface Outputs{
        fun finishLoadingUserInfo() : Observable<UserInformationResult>
        fun didFinishWithError() : Observable<String>
        fun backToWelcome() : Observable<Unit>
        fun retryLoading() : Observable<Boolean>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<SplashViewModel>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()
        private val finishAnimation = PublishSubject.create<Unit>()
        private val retryButtonPressed = PublishSubject.create<Unit>()
        private val backButtonPressed = PublishSubject.create<Unit>()

        //Outputs
        private val finishLoadingUserInfo:  Observable<UserInformationResult>
        private val didFinishWithError : Observable<String>
        private val backToWelcome = BehaviorSubject.create<Unit>()
        private val retryLoading = BehaviorSubject.create<Boolean>()

        init {
            backButtonPressed
                .flatMap { environment.authenticationUseCase().signOut() }
                .map { Unit }
                .subscribe(this.backToWelcome)

            val mainEvent = Observable.merge(onCreate,retryButtonPressed)
                .flatMap { return@flatMap getUserInfoAndSubscribe() }
                .share()

            finishLoadingUserInfo = mainEvent
                .filter { !it.isFail() }
                .zipWith(finishAnimation)
                .map { it.first.successValue() }

            didFinishWithError = mainEvent
                .filter { it.isFail() }
                .zipWith(finishAnimation)
                .map { "Error" }

        }

        override fun onCreate() {
            this.onCreate.onNext(Unit)
        }

        override fun finishAnimation() {
            this.finishAnimation.onNext(Unit)
        }

        override fun retryButtonPressed() {
            this.retryButtonPressed.onNext(Unit)
        }

        override fun backButtonPressed() {
            this.backButtonPressed.onNext(Unit)
        }

        override fun finishLoadingUserInfo(): Observable<UserInformationResult> = this.finishLoadingUserInfo

        override fun didFinishWithError(): Observable<String> = this.didFinishWithError

        override fun backToWelcome(): Observable<Unit> = this.backToWelcome

        override fun retryLoading(): Observable<Boolean> = this.retryLoading

        private fun getUserInfoAndSubscribe() : Observable<Result<UserInformationResult>>{
            Log.e("🔸", "getUserInfoAndSubscribe")
            return Observable.create<Result<UserInformationResult>> create@{ o ->
                val getUserEvent = environment.userUseCase().getUserInformation(false)
                    .share()
                getUserEvent
                    .filter { !it.isFail() }
                    .map { it.successValue() }
                    .subscribe{
                        if (it == null) return@subscribe
                        environment.userUseCase().initServerSubscription(it.userId)
                        val result = Result.success(it)
                        o.onNext(result)
                        o.onComplete()
                    }
                getUserEvent
                    .filter { it.isFail() }
                    .map {
                        when(it){
                            is Result.success -> return@map null
                            is Result.failure -> return@map it.cause
                        }
                    }
                    .subscribe { error ->
                        o.onNext(Result.failure(error))
                        o.onComplete()
                    }

            }
                .doOnComplete { this.retryLoading.onNext(false) }
                .doOnSubscribe { this.retryLoading.onNext(true) }
            /*
            return environment.userUseCase().getUserInformation(false)
                .doOnComplete { this.retryLoading.onNext(false) }
                .doOnSubscribe { this.retryLoading.onNext(true) }*/
        }
    }
}