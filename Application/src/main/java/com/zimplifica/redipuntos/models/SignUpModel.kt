package com.zimplifica.redipuntos.models

import com.zimplifica.redipuntos.libs.utils.ValidationService
import java.io.Serializable

class SignUpModel(val userId : String, val phoneNumber : String, val password: String,val nickname: String): Serializable {
    fun phoneNumberWithExtension() : String{
        return ValidationService.normalizePhoneNumber(phoneNumber)?:""
    }

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is SignUpModel) return false
        return userId == other.userId
    }
}