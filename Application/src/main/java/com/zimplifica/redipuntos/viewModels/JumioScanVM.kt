package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

interface JumioScanVM {
    interface Inputs {
        fun scanActivity()
        fun finishVerification(state : Boolean)
    }
    interface Outputs{
        fun openScan() : Observable<Unit>
        fun stepCompleted() : Observable<Unit>
        fun stepError() : Observable<Unit>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<JumioScanVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val scanActivity = PublishSubject.create<Unit>()
        private val finishVerification = PublishSubject.create<Boolean>()

        //Outputs
        private val openScan = BehaviorSubject.create<Unit>()
        private val stepCompleted = BehaviorSubject.create<Unit>()
        private val stepError = BehaviorSubject.create<Unit>()

        init {
            this.scanActivity
                .subscribe(this.openScan)

            finishVerification
                .filter { it }
                .flatMap { environment.userUseCase().initIdentitiyVerification() }
                .share()
                .delay(2,TimeUnit.SECONDS,AndroidSchedulers.mainThread())
                .map { Unit }
                .subscribe(this.stepCompleted)
            
            finishVerification
                .filter { !it }
                .map { Unit }
                .subscribe(this.stepError)
        }

        override fun finishVerification(state: Boolean) {
            this.finishVerification.onNext(state)
        }

        override fun stepCompleted(): Observable<Unit> = this.stepCompleted

        override fun stepError(): Observable<Unit> = this.stepError

        override fun scanActivity() {
            return this.scanActivity.onNext(Unit)
        }

        override fun openScan(): Observable<Unit> = this.openScan
    }
}