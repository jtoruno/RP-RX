package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface JumioScanVM {
    interface Inputs {
        fun scanActivity()
    }
    interface Outputs{
        fun openScan() : Observable<Unit>
    }
    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<JumioScanVM>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val scanActivity = PublishSubject.create<Unit>()

        //Outputs
        private val openScan = BehaviorSubject.create<Unit>()

        init {
            this.scanActivity
                .subscribe(this.openScan)
        }

        override fun scanActivity() {
            return this.scanActivity.onNext(Unit)
        }

        override fun openScan(): Observable<Unit> = this.openScan
    }
}