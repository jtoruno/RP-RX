package com.zimplifica.domain.entities

class RateCommerceModel(var id : String, var commerceName: String, var date: String,rate: CommercesRate = CommercesRate.NOT_RATED) {
    var rate : CommercesRate
    init {
        this.rate = rate
    }

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is RateCommerceModel) return false
        return other.id == id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + commerceName.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + rate.hashCode()
        return result
    }
}

enum class CommercesRate(val int: Int){
    NOT_RATED(0),BAD(1),POOR(2),REGULAR(3),GOOD(4),EXCELLENT(5)
}

