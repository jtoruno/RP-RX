package com.zimplifica.redipuntos.viewModels

import android.support.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface HelpViewModel {
    interface Inputs {
        fun termsButtonClicked()
        fun privacyButtonClicked()
    }
    interface Outputs {
        fun startTermsWebView() : Observable<Unit>
        fun startPrivacyWebView() : Observable<Unit>
    }

    class ViewModel(@NonNull val environment: Environment):ActivityViewModel<HelpViewModel>(environment),Inputs,Outputs{
        val inputs : Inputs = this
        val outputs: Outputs = this

        //Inputs
        private val termsButtonClicked = PublishSubject.create<Unit>()
        private val privacyButtonClicked = PublishSubject.create<Unit>()

        //Outputs
        private val startTermsWebView : Observable<Unit>
        private val startPrivacyWebView : Observable<Unit>

        init {
            this.startTermsWebView = this.termsButtonClicked
            this.startPrivacyWebView = this.privacyButtonClicked
        }

        override fun termsButtonClicked() = this.termsButtonClicked.onNext(Unit)

        override fun privacyButtonClicked() = this.privacyButtonClicked.onNext(Unit)

        override fun startTermsWebView(): Observable<Unit> = this.startTermsWebView

        override fun startPrivacyWebView(): Observable<Unit> = this.startPrivacyWebView

    }
}