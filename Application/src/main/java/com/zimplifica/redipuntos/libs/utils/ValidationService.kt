package com.zimplifica.redipuntos.libs.utils

import android.util.Patterns
import java.util.regex.Pattern

enum class UserCredentialType {
    PHONE_NUMBER, EMAIL
}

object ValidationService {
    private val minPasswordLenght = 8
    private val maxPasswordLenght = 20
    private val pattern1 = Pattern.compile(".*[A-Z].*")
    private val pattern1Sub = Pattern.compile(".*[a-z].*")
    private val pattern2 = Pattern.compile("\\d{8}")
    private val pattern3 = Pattern.compile(".*[!\$#@_.+-].*")
    private val allowedPhoneNumbers = mutableListOf("4","5","6","7","8","9")

    fun userCredentialType(username: String) : UserCredentialType {
        if(isNumberWithExtension(username) || isNumberWithoutExtension(username)){
            return UserCredentialType.PHONE_NUMBER
        }else{
            return UserCredentialType.EMAIL
        }
    }

    private fun isValidEmail(username : String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isNumberWithExtension(number : String) : Boolean{
        return number.contains("+506") && pattern2.matcher(number).matches()
    }

    private fun isNumberWithoutExtension(number: String) : Boolean{
        return pattern2.matcher(number).matches()
    }

    fun validateUserCredentials(username: String, password : String) : Boolean{
        var isValid = false
        if( password.length < minPasswordLenght || password.length> maxPasswordLenght) {
            return false
        }

        if(isNumberWithExtension(username) || isNumberWithoutExtension(username)){
            isValid = true
        } else {
            if (isValidEmail(username)) {
                isValid = true
            }
        }
        return isValid
    }

    fun normalizePhoneNumber(phoneNumber : String) : String?{
        if(isNumberWithExtension(phoneNumber)){
            return phoneNumber
        }else if (isNumberWithoutExtension(phoneNumber)){
            return "+506$phoneNumber"
        } else {return null}
    }

    fun validateVerificationCode(verificationCode: String) : Boolean{
        val pattern = Pattern.compile("[0-9]{6}")
        return pattern.matcher(verificationCode).matches()
    }

    fun validateUsername(username: String) : Boolean{
        if(username.isEmpty()){return false}
        if(!isValidEmail(username) && !isNumberWithExtension(username) && !isNumberWithoutExtension(username)){return false}
        return true
    }
}