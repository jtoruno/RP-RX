package com.zimplifica.domain.entities

class UserInformationResult(var id: String,var nickname: String?,var userEmail: String?,
                            var userPhoneNumber: String, var userPhoneVerified: Boolean, var userEmailVerified: Boolean,
                             var rewards: Double?, var paymentMethods: List<PaymentMethod>, var securityCodeCreated: Boolean){
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is UserInformationResult) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (nickname?.hashCode() ?: 0)
        result = 31 * result + (userEmail?.hashCode() ?: 0)
        result = 31 * result + userPhoneNumber.hashCode()
        result = 31 * result + userPhoneVerified.hashCode()
        result = 31 * result + userEmailVerified.hashCode()
        result = 31 * result + (rewards?.hashCode() ?: 0)
        result = 31 * result + paymentMethods.hashCode()
        result = 31 * result + securityCodeCreated.hashCode()
        return result
    }


}

class UserStatus(val status : VerificationStatus, val verificationReference : String?)

enum class VerificationStatus{
    Pending, Verifying, VerifiedValid, VerifiedInvalid
}

enum class Gender{
    Male, Female, Other
}