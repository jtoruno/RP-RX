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
        fun finishVerificationOutput() : Observable<Boolean>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<JumioScanVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val scanActivity = PublishSubject.create<Unit>()
        private val finishVerification = PublishSubject.create<Boolean>()

        //Outputs
        private val openScan = BehaviorSubject.create<Unit>()
        private val finishVerificationOutput = BehaviorSubject.create<Boolean>()


        init {
            this.scanActivity
                .subscribe(this.openScan)
            this.finishVerification
                .subscribe(this.finishVerificationOutput)
        }

        override fun finishVerificationOutput(): Observable<Boolean> = this.finishVerificationOutput

        override fun finishVerification(state: Boolean) {
            this.finishVerification.onNext(state)
        }

        override fun scanActivity() {
            return this.scanActivity.onNext(Unit)
        }

        override fun openScan(): Observable<Unit> = this.openScan
    }
}