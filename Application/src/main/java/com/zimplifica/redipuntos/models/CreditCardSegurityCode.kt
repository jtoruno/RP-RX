package com.zimplifica.redipuntos.models

class CreditCardSegurityCode(var code : String, var issuer : CreditCardNumber.Issuer) {
    val valueFormatted by lazy { handleCharactersLimit() }
    val isValid by lazy { validateSecurityCode() }
    private fun handleCharactersLimit(): String{
        var v = code
        when(issuer){
            CreditCardNumber.Issuer.AMEX -> {
                if(code.length > 4){
                    v = code.substring(0,4)
                }
            }
            else -> {
                if(code.length > 3){
                    v = code.substring(0,3)
                }
            }
        }
        return v
    }

    private fun validateSecurityCode() : Boolean{
        var v = handleCharactersLimit()
        return when(issuer){
            CreditCardNumber.Issuer.AMEX -> {
                v.length == 4
            }
            else -> {
                v.length == 3
            }
        }
    }
}