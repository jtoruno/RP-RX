package com.zimplifica.domain.entities

class PaymentInformation(rediPoints: Double, cardPoints: Double, subtotal : Double,fee: Double,tax : Double,total: Double, cashback : Int, taxes : Int) {
    var rediPoints: Double
    var cardPoints: Double
    var subtotal : Double
    var total: Double
    var totalPointsValue: Double
    var usedRediPoints: Double
    var usedCardPoints: Double
    var usedTotalPointsValue: Double
    var cardAmountToPay: Double
    var fee: Double
    var tax: Double

    init {
        this.rediPoints = rediPoints
        this.cardPoints = cardPoints
        this.subtotal = subtotal
        this.total = total
        this.totalPointsValue = rediPoints + cardPoints
        this.fee = fee
        this.tax = tax


        //RedPuntos
        val auxRedipuntos = this.total - this.rediPoints
        if(auxRedipuntos > 0){
            this.usedRediPoints = this.total - auxRedipuntos
        }else{
            this.usedRediPoints = this.rediPoints
        }

        //CardPoints
        val cardPointsAux = this.subtotal - this.usedRediPoints
        if(cardPointsAux <= this.cardPoints){
            this.usedCardPoints = cardPointsAux
        }else{
            this.usedCardPoints = this.cardPoints
        }

        this.usedTotalPointsValue = this.usedRediPoints + this.usedCardPoints

        // Card Amount To Pay
        this.cardAmountToPay = this.total - this.usedRediPoints
    }


}