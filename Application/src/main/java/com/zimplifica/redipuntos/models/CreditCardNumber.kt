package com.zimplifica.redipuntos.models

class CreditCardNumber(var value : String) {
    enum class Issuer {
        AMEX, MASTERCARD, VISA, DISCOVER, UNKNOWN
    }

    enum class Status { invalid, valid, unkown
    }

    var issuer : Issuer
    val valueFormatted : String by lazy { formatCardNumber()  }
    val status : Status by lazy { getStatusCard() }

    init {
        value = value.trim()
        var issuer : Issuer = Issuer.UNKNOWN
        if(value.startsWith("4")){
            issuer = Issuer.VISA
        }
        if((value.startsWith("34"))|| (value.startsWith("37"))){
            issuer = Issuer.AMEX
        }
        if ((value.startsWith("51")) || (value.startsWith("52")) || (value.startsWith("53")) || (value.startsWith("54")) || (value.startsWith("55"))){
            issuer = Issuer.MASTERCARD
        }
        if((value.startsWith("65")) || (value.startsWith("644")) || (value.startsWith("6011"))){
            issuer = Issuer.DISCOVER
        }
        this.issuer = issuer
    }

    private fun formatCardNumber() : String{
        val cardNumber = handleLimitOfCharacters()
        when(issuer){
            Issuer.AMEX -> {
                return formatAmex(cardNumber)
            }
            Issuer.MASTERCARD, Issuer.VISA -> {
                return formatVisaOrMasterCard(cardNumber)
            }
            else->{
                return cardNumber
            }
        }
    }

    private fun handleLimitOfCharacters() : String{
        when(issuer){
            Issuer.AMEX -> {
                if(value.length > 15){
                    return value.substring(0,15)
                }
            }
            Issuer.VISA , Issuer.MASTERCARD-> {
                if(value.length > 16){
                    return value.substring(0,16)
                }
            }
            else -> {
                if (value.length > 19){
                    return value.substring(0,19)
                }
            }
        }
        return value
    }

    private fun getStatusCard() : Status{
        val cardNumber = handleLimitOfCharacters()
        when(issuer){
            Issuer.AMEX -> {
                if(cardNumber.length < 15){
                    return Status.unkown
                }
            }
            Issuer.VISA, Issuer.MASTERCARD -> {
                if(cardNumber.length < 16){
                    return Status.unkown
                }
            }
            else -> {
                if(cardNumber.length < 19){
                    return Status.unkown
                }
            }
        }
        val check = luhnCheck(value)
        return if(check){
            Status.valid
        } else {
            Status.invalid
        }
    }

    private fun validateCreditCard(cardNum: String) : Status{
        var status = Status.unkown
        when(issuer){
            Issuer.AMEX -> {
                if(value.length < 15){
                    status = Status.unkown
                }
            }
            Issuer.MASTERCARD, Issuer.VISA -> {
                if(value.length < 16){
                    status = Status.unkown
                }
            }
            else -> {
                if(value.length < 19){
                    status = Status.unkown
                }
            }
        }
        val check = luhnCheck(value)
        status = if(check){
            Status.valid
        } else {
            Status.invalid
        }
        return status

    }

    private fun formatAmex(creditCard : String): String{
        if(creditCard.isEmpty()){return ""}
        var number = creditCard
        if(number.length > 10){
            number = number.substring(0,4)+" "+number.substring(4,10)+" "+number.substring(10,number.length)
        }
        else if(number.length > 4){
            number = number.substring(0,4)+" "+number.substring(4,number.length)
        }
        return number
    }

    private fun formatVisaOrMasterCard(creditCard : String): String{
        if(creditCard.isEmpty()){return ""}
        var number = creditCard
        if(number.length > 12){
            number = number.substring(0,4)+" "+number.substring(4,8)+" "+number.substring(8,12)+" "+number.substring(12,number.length)
        }
        else if(number.length > 8){
            number = number.substring(0,4)+" "+number.substring(4,8)+" "+number.substring(8,number.length)
        }
        else if (number.length > 4){
            number = number.substring(0,4)+" "+number.substring(4,number.length)
        }
        return number
    }

    private fun luhnCheck(cardNumber: String): Boolean {
        val reversed = StringBuffer(cardNumber).reverse().toString()
        val len = reversed.length
        var oddSum = 0
        var evenSum = 0
        for (i in 0 until len) {
            val c = reversed[i]
            if (!Character.isDigit(c)) {
                throw IllegalArgumentException(String.format("Not a digit: '%s'", c))
            }
            val digit = Character.digit(c, 10)
            if (i % 2 == 0) {
                oddSum += digit
            } else {
                evenSum += digit / 5 + 2 * digit % 10
            }
        }
        return (oddSum + evenSum) % 10 == 0
    }
}