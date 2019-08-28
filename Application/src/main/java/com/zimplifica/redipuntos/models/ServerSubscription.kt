package com.zimplifica.redipuntos.models

import com.zimplifica.domain.entities.RediSubscription

object ServerSubscription {
    private var subscription : RediSubscription ? = null
    fun updateServerSubscription(subscription : RediSubscription?){
        this.subscription = subscription
    }
    fun getServerSubscription() : RediSubscription? {
        return this.subscription
    }
}