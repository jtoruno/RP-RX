package com.zimplifica.redipuntos.mocks

import com.zimplifica.domain.entities.RediSubscription
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.ServerEvent
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ServerSubscriptionTest (val userId : String) : RediSubscription {
    private var events = PublishSubject.create<Result<ServerEvent>>()
    private var baseEvent = BehaviorSubject.create<Result<List<ServerEvent>>>()
    override fun subscribeToEvents(): Observable<Result<ServerEvent>> {
        return events
    }

    override fun subscribeToBaseEvents(): Observable<Result<List<ServerEvent>>> {
         return baseEvent
    }

    override fun cancel() {
    }
}