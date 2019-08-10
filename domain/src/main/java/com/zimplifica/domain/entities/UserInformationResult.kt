package com.zimplifica.domain.entities

class UserInformationResult(var userId: String,var citizenId: String?, var userFirstName: String?,
                            var userLastName: String?, var userBirthDate: String?, var userEmail: String?,
                            var userPhoneNumber: String, var userPhoneVerified: Boolean, var userEmailVerified: Boolean,
                            var userPicture: String?, var rewards: Double?, var paymentMethods: List<PaymentMethod>, var status: UserStatus)

class UserStatus(val status : VerificationStatus, val verificationReference : String?)

enum class VerificationStatus{
    Pending, Verifying, VerifiedValid, VerifiedInvalid
}