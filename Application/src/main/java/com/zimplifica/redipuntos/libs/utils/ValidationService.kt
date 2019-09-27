package com.zimplifica.redipuntos.libs.utils

import android.util.Log
import android.util.Patterns
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.VerificationStatus
import java.util.regex.Pattern

enum class UserCredentialType {
    PHONE_NUMBER, EMAIL
}

enum class ProfileStep {
    PhoneNumber, Email, VerifyEmail,
    PaymentMethod, Unknown
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

    // Validate the nickname and username.
    fun validateNicknameAndUsername(nickname: String?,username: String?) : Boolean {
        if(nickname == null || username==null) { return false }
        if (nickname.isEmpty() || !validatePhoneNumber(username)) { return false }
        return true
    }

    /// Validate the phone number lenght.
    fun validatePhoneNumber(phoneText: String?) : Boolean {
        if (phoneText.isNullOrEmpty()) { return false }

        if (phoneText.length != 8) { return false }

        val phoneStart = phoneText.first()
        if (!allowedPhoneNumbers.contains(phoneStart.toString())){ return false }
        return true
    }

    fun isValidEmail(username : String): Boolean{
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

    private fun validatePasswordLenght(password : String) : Boolean{
        val minPasswordLenght = 8
        val maxPasswordLenght = 20
        if(password.length < minPasswordLenght || password.length > maxPasswordLenght){
            return false
        }
        return true
    }

    private fun validatePasswordCapitalLowerLetters(password: String) : Boolean {
        val pattern1 = Pattern.compile(".*[A-Z].*")
        val pattern1Sub = Pattern.compile(".*[a-z].*")
        if (pattern1.matcher(password).matches() && pattern1Sub.matcher(password).matches()){
            return true
        }
        return false
    }

    private fun validatePasswordNumbers(password: String): Boolean{
        val pattern2 = Pattern.compile(".*\\d.*")
        if(pattern2.matcher(password).matches()){
            return true
        }
        return false
    }

    private fun validatePasswordSpecialCharacters(password: String) : Boolean {
        val pattern3 = Pattern.compile(".*[!\$#@_.+-].*")
        if(pattern3.matcher(password).matches()){
            return true
        }
        return false
    }

    fun validatePassword(password: String) : Boolean {
        var isSuccess = true
        if (!validatePasswordLenght(password)){isSuccess = false}
        if (!validatePasswordCapitalLowerLetters(password)){isSuccess = false}
        if (!validatePasswordNumbers(password)){isSuccess = false}
        if (!validatePasswordSpecialCharacters(password)){isSuccess = false}

        return isSuccess
    }

    fun validateConfirmForgotPassword(confirmationCode : String, password: String) : Boolean{
        val confirmationCodeValidation = validateVerificationCode(confirmationCode)
        val passwordValidation = validatePassword(password)
        return confirmationCodeValidation && passwordValidation
    }

    /// Validate if the verification and the security code are valid.
    fun validateVerificationAndSecurityCode(pin: String?, verificationCode: String?) : Boolean {
        if(pin == null || verificationCode == null) { return false }
        if (verificationCode.length != 6 || !validateSecurityCode( pin)) { return false }
        return true
    }

    /// Validate if the security code is valid.
    fun validateSecurityCode(securityCode: String?) : Boolean {
        if(securityCode == null) { return false }
        val  numberRegEx  = Pattern.compile("[0-9]{4}")
        if (numberRegEx.matcher(securityCode).matches()) { return true }
        return false
    }

    fun getNextStepToCompleteProfile(userInfo: UserInformationResult?) : ProfileStep?{
        if(userInfo==null){return ProfileStep.Unknown}
        if(userInfo.userEmail == null){
            return ProfileStep.Email
        }
        if(!userInfo.userEmailVerified){
            return ProfileStep.VerifyEmail
        }
        if (userInfo.paymentMethods.isEmpty()){
            return ProfileStep.PaymentMethod
        }
        return null
    }

    fun getCompletedStepsCount(userInfo: UserInformationResult?) : Int{
        if(userInfo==null){return 0}
        var count = 0

        if(userInfo.userEmailVerified && userInfo.userEmail != null){
            count +=1
        }
        if (userInfo.paymentMethods.isNotEmpty()){
            count +=1
        }
        return count
    }
}

