package com.zimplifica.redipuntos.models

import com.zimplifica.redipuntos.libs.utils.ValidationService

class SignUpModel(val userId : String, val phoneNumber : String, val password: String) {
    fun phoneNumberWithExtension() : String{
        return ValidationService.normalizePhoneNumber(phoneNumber)?:""
    }
}