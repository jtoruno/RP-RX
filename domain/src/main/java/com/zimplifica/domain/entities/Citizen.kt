package com.zimplifica.domain.entities

import java.text.SimpleDateFormat
import java.util.*

class Citizen(val lastName: String, val firstName: String, val birthdate: Date, val identityNumber: String) {

    fun getBirthDateAsString() : String{
        val dateF = SimpleDateFormat("dd/MM/yyyy")
        return dateF.format(this.birthdate)
    }
}