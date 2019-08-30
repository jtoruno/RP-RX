package com.zimplifica.redipuntos.viewModels

import androidx.annotation.NonNull
import com.zimplifica.domain.entities.ServerEvent
import com.zimplifica.redipuntos.libs.ActivityViewModel
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface NotificationsVM {
    interface Inputs {
        fun onCreate()
        fun swiped()
    }
    interface Outputs {
        fun updateNotifications() : Observable<List<ServerEvent>>
    }
    class ViewModel(@NonNull val environment: Environment): ActivityViewModel<NotificationsVM>(environment), Inputs , Outputs{

        val inputs : Inputs = this
        val outputs : Outputs = this

        //Inputs
        private val onCreate = PublishSubject.create<Unit>()
        private val swiped = PublishSubject.create<Unit>()

        //Outputs
        private val updateNotifications = BehaviorSubject.create<List<ServerEvent>>()

        init {
            Observable.merge(this.onCreate,this.swiped)
                .flatMap { environment.userUseCase().getNotificationsSubscription() }
                .map { notifications ->
                    return@map notifications.filter { !it.hidden }
                }
                .subscribe(this.updateNotifications)

        }

        override fun onCreate() {
            return this.onCreate.onNext(Unit)
        }

        override fun swiped() {
            return this.swiped.onNext(Unit)
        }

        override fun updateNotifications(): Observable<List<ServerEvent>> = this.updateNotifications

    }
}