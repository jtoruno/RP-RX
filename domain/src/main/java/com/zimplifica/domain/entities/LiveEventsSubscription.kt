package com.zimplifica.domain.entities

import io.reactivex.Observable

enum class SubscriptionStatus {
    Connected, Connecting, Disconected, Error
}

interface RediSubscription {
    fun subscribeToEvents() : Observable<Result<ServerEvent>>
    fun subscribeToBaseEvents() : Observable<Result<List<ServerEvent>>>
    fun cancel()
}