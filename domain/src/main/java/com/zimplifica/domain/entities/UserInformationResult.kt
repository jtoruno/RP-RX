package com.zimplifica.domain.entities

class UserInformationResult(val userId: String,val citizenId: String?, val userFirstName: String?,
                            val userLastName: String?, val userBirthDate: String?, val userEmail: String?,
                            val userPhoneNumber: String, val userPhoneVerified: Boolean, val userEmailVerified: Boolean,
                            val userPicture: String?, val rewards: Double?, val paymentMethods: List<PaymentMethod>) {
}