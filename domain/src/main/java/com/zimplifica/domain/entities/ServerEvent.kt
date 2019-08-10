package com.zimplifica.domain.entities

class ServerEvent(val id: String,val type: String,val data: String?) {
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is ServerEvent) return false
        return id == other.id
    }
}