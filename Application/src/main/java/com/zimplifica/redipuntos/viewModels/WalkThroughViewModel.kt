package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface WalkThroughViewModel {
    interface Inputs {
        fun skipButtonClicked()
        fun nextButtonClicked()
        fun backButtonClicked()
    }
    interface Outputs {
        fun startNextActivity(): Observable<Unit>
        fun startBackActivity(): Observable<Unit>
        fun startMainActivity(): Observable<Unit>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<WalkThroughViewModel>(environment), Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val skipButtonClicked = PublishSubject.create<Unit>()
        private val nextButtonClicked = PublishSubject.create<Unit>()
        private val backButtonClicked = PublishSubject.create<Unit>()


        //Outputs
        private val startNextActivity : Observable<Unit>
        private val startMainActivity : Observable<Unit>
        private val startBackActivity : Observable<Unit>

        init {
            this.startNextActivity = this.nextButtonClicked
            this.startMainActivity = this.skipButtonClicked
            this.startBackActivity = this.backButtonClicked
        }


        override fun skipButtonClicked() = this.skipButtonClicked.onNext(Unit)

        override fun nextButtonClicked() = this.nextButtonClicked.onNext(Unit)

        override fun startNextActivity(): Observable<Unit> = this.startNextActivity

        override fun startMainActivity(): Observable<Unit> = this.startMainActivity

        override fun backButtonClicked() = this.backButtonClicked.onNext(Unit)

        override fun startBackActivity(): Observable<Unit> = this.startBackActivity

    }
}