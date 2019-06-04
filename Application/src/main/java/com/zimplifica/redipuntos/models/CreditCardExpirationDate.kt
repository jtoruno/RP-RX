package com.zimplifica.redipuntos.models

import java.text.SimpleDateFormat
import java.util.*

class CreditCardExpirationDate(var value : String) {
    val isValid by lazy { validateDate() }
    val fullDate by lazy { FullDate() }
    val valueFormatted by lazy { formatDate() }

    enum class ExpirationDateStatus{
        invalid, valid, unkown
    }

    private fun formatDate() : String{
        val d = handleCharactersLimit()
        if (d.isEmpty()){return ""}
        var number = d
        if(d.length >= 2){
            number = number.substring(0,2)+"/"+number.substring(3,number.length)
        }
        return number
    }

    private fun handleCharactersLimit() : String{
        return if(value.length > 5){
            value.substring(0,5)
        } else{
            value
        }
    }

    private fun validateDate() : ExpirationDateStatus{
        val number = handleCharactersLimit()
        if(number.length == 5){
            val month = number.substring(0,2)
            val year = number.substring(3,number.length)
            val dateAsString = "01/$month/20$year"
            val date = SimpleDateFormat("dd/MM/yyyy").parse(dateAsString)
            if (date > Date()){
                return ExpirationDateStatus.valid
            }
            else{
                return ExpirationDateStatus.invalid
            }
        }
        else{
            return ExpirationDateStatus.unkown
        }
    }

    private fun FullDate() : String? {
        val number = handleCharactersLimit()
        if(validateDate() == ExpirationDateStatus.valid){
            val month = number.substring(0,2)
            val year = number.substring(3,number.length)
            return "01/$month/20$year"
        }
        else{
            return null
        }
    }

}