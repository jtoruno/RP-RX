package com.zimplifica.domain.entities

class PaymentInformation(rediPoints: Double, cardPoints: Double, totalAmount: Double) {
    var rediPoints: Double
    var cardPoints: Double
    var totalAmount: Double
    var totalPointsValue: Double
    var usedRediPoints: Double
    var usedCardPoints: Double
    var usedTotalPointsValue: Double
    var cardAmountToPay: Double

    init {
        this.rediPoints = rediPoints
        this.cardPoints = cardPoints
        this.totalAmount = totalAmount
        this.totalPointsValue = rediPoints + cardPoints

        //RedPuntos
        val auxRedipuntos = this.totalAmount - this.rediPoints
        if(auxRedipuntos > 0){
            this.usedRediPoints = this.totalAmount - auxRedipuntos
        }else{
            this.usedRediPoints = this.totalAmount
        }

        //CardPoints
        val cardPointsAux = this.totalAmount - this.usedRediPoints
        if(cardPointsAux <= this.cardPoints){
            this.usedCardPoints = cardPointsAux
        }else{
            this.usedCardPoints = this.cardPoints
        }

        this.usedTotalPointsValue = this.usedRediPoints + this.usedCardPoints

        // Card Amount To Pay
        this.cardAmountToPay = this.totalAmount - this.usedTotalPointsValue
    }


}