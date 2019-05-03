package com.zimplifica.redipuntos.libs

import com.trello.rxlifecycle.ActivityEvent
import io.reactivex.Observable


interface ActivityLifecycleType {

    /**
     * An observable that describes the lifecycle of the object, from CREATE to DESTROY.
     */
    fun lifecycle(): Observable<ActivityEvent>
}