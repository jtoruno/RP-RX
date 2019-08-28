package com.zimplifica.domain.entities

class ServerEvent(val id: String,val type: String,val title: String, val message: String, val createdAt: String,
                  val data: String?, val actionable : Boolean, val triggered: Boolean, val hidden : Boolean) {
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is ServerEvent) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + actionable.hashCode()
        result = 31 * result + triggered.hashCode()
        result = 31 * result + hidden.hashCode()
        return result
    }
}