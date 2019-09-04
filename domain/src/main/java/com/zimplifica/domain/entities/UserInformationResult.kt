package com.zimplifica.domain.entities

class UserInformationResult(var userId: String,var citizenId: String?, var userFirstName: String?,
                            var userLastName: String?, var userBirthDate: String?, var userEmail: String?,
                            var userPhoneNumber: String, var userPhoneVerified: Boolean, var userEmailVerified: Boolean,
                            var userPicture: String?, var rewards: Double?, var paymentMethods: List<PaymentMethod>, var status: UserStatus){
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is UserInformationResult) return false
        return userId == other
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + (citizenId?.hashCode() ?: 0)
        result = 31 * result + (userFirstName?.hashCode() ?: 0)
        result = 31 * result + (userLastName?.hashCode() ?: 0)
        result = 31 * result + (userBirthDate?.hashCode() ?: 0)
        result = 31 * result + (userEmail?.hashCode() ?: 0)
        result = 31 * result + userPhoneNumber.hashCode()
        result = 31 * result + userPhoneVerified.hashCode()
        result = 31 * result + userEmailVerified.hashCode()
        result = 31 * result + (userPicture?.hashCode() ?: 0)
        result = 31 * result + (rewards?.hashCode() ?: 0)
        result = 31 * result + paymentMethods.hashCode()
        result = 31 * result + status.hashCode()
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