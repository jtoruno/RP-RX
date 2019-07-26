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
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface SplashViewModel {
    interface Inputs {
        fun onCreate()
        fun token(token : String)
    }
    interface Outputs{
        //fun splashAction() : Observable<UserStateResult>
        fun signedInAction(): Observable<Unit>
        fun signedOutAction(): Observable<Unit>
        fun tokenAction() : Observable<String>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<SplashViewModel>(environment), Inputs, Outputs{



        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()
        private val token = PublishSubject.create<String>()

        //Outputs
        private val signedInAction = BehaviorSubject.create<Unit>()
        private val signedOutAction = BehaviorSubject.create<Unit>()
        private val tokenAction = BehaviorSubject.create<String>()

        init {

            val registDeviceEvent = token
                .flatMap { this.registDeviceToken(it) }
                .share()

            registDeviceEvent
                .filter { it.isFail() }
                .subscribe { Log.e("Error","Error with the token device") }

            registDeviceEvent
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe(this.tokenAction)


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

        override fun token(token: String) {
            return this.token.onNext(token)
        }

        override fun signedInAction(): Observable<Unit> = this.signedInAction

        override fun signedOutAction(): Observable<Unit> = this.signedOutAction
        /*
        override fun splashAction(): Observable<UserStateResult> {
            return this.splashAction
        }*/

        override fun tokenAction(): Observable<String> = this.tokenAction

        private fun submit() : Observable<UserStateResult>{
            Log.e("Print",environment.currentUser().getCurrentUser().toString())
            return environment.authenticationUseCase().getCurrentUserState()
        }

        private fun finishLoadingUserInfo() : Observable<Result<UserInformationResult>>{
            return environment.userUseCase().getUserInformation(false)
                    /*
                .doAfterNext{
                    Log.e("doAfterNext", "Log")
                    when(it){
                        is Result.success -> {
                            Log.e("doAfterNext", "sucesss")
                            FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.w("SplashActivity", "getInstanceId failed", task.exception)
                                        return@OnCompleteListener
                                    }
                                    // Get new Instance ID token
                                    val token = task.result?.token
                                    this.registDeviceToken(token?:"")
                                })
                        }
                        is Result.failure -> {
                            Log.e("SplashVM", "Error, set token device")
                        }
                    }
                }*/
        }

        private fun registDeviceToken(token : String) : Observable<Result<String>>{
            val userId = environment.currentUser().getCurrentUser()?.userId
            return environment.userUseCase().registPushNotificationToken(token, userId ?: "")

        }

    }
}