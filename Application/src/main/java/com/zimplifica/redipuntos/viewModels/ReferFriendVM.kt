package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.ui.data.referFriendBaseLink
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface ReferFriendVM {
    interface Inputs {
        /// Call when share button is pressed.
        fun shareButtonPressed()

        /// Call when copy to clipboard button is pressed.
        fun copyToClipboardPressed()
    }
    interface Outputs {
        /// Emits when share button is pressed.
        fun shareButtonAction() : Observable<String>

        /// Emits when copy to clipboard button is pressed.
        fun copyToClipboardAction() : Observable<String>
    }

    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<ReferFriendVM>(environment),Inputs, Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val shareButtonPressed = PublishSubject.create<Unit>()
        private val copyToClipboardPressed = PublishSubject.create<Unit>()

        //Outputs
        private val shareButtonAction = BehaviorSubject.create<String>()
        private val copyToClipboardAction = BehaviorSubject.create<String>()

        init {
            shareButtonPressed
                .flatMap { return@flatMap environment.userUseCase().getUserInformation(true) }
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe {userInfo ->
                    val userId = userInfo?.id ?: ""
                    this.shareButtonAction.onNext("$referFriendBaseLink${userId}")
                }

            copyToClipboardPressed
                .flatMap { return@flatMap environment.userUseCase().getUserInformation(true) }
                .filter { !it.isFail() }
                .map { it.successValue() }
                .subscribe {
                    val userId = it?.id ?: ""
                    this.copyToClipboardAction.onNext("$referFriendBaseLink${userId}")
                }
        }

        override fun shareButtonPressed() {
            return this.shareButtonPressed.onNext(Unit)
        }

        override fun copyToClipboardPressed() {
            return this.copyToClipboardPressed.onNext(Unit)
        }

        override fun shareButtonAction(): Observable<String> = this.shareButtonAction

        override fun copyToClipboardAction(): Observable<String> = this.copyToClipboardAction

    }
}