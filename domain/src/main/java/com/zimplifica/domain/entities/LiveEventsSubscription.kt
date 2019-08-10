package com.zimplifica.domain.entities

import io.reactivex.Observable

enum class SubscriptionStatus {
    Connected, Connecting, Disconected, Error
}

interface RediSubscription {
    fun subscribeToEvents() : Observable<Result<ServerEvent>>
    fun subscribeToStatus() : Observable<SubscriptionStatus>
    fun cancel()
}