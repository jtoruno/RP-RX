package com.zimplifica.redipuntos.mocks

import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.domain.entities.UserInformationResult

val creditCardMock : List<PaymentMethod> = mutableListOf(PaymentMethod("1234321412341234", "1234", "11/22", "VISA"))
fun userInformationMock(id: String = "550e8400-e29b-41d4-a716-446655440000", nickname: String = "Jtoru", userEmail: String = "zimple@zimplifica.com", userPhoneNumber: String = "+50689626004", userPhoneVerified: Boolean = true, userEmailVerified: Boolean = true, rewards: Double? = null, paymentMethods: List<PaymentMethod> = creditCardMock, securityCodeCreated: Boolean = false) : UserInformationResult {
    return UserInformationResult(id,nickname,userEmail,userPhoneNumber,userPhoneVerified,userEmailVerified,rewards,paymentMethods,securityCodeCreated)
}