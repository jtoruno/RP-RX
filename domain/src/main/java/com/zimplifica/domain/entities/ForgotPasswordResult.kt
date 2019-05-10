package com.zimplifica.domain.entities


enum class UserCodeDeliveryMedium {
    sms, email, unknown
}

enum class ForgotPasswordState {
    done, confirmationCodeSent
}
data class UserCodeDeliveryDetails(var deliveryMedium: UserCodeDeliveryMedium, var destination : String?, var attributeName: String?)

class ForgotPasswordResult(forgotPasswordState: ForgotPasswordState, userCodeDeliveryDetails: UserCodeDeliveryDetails){
    var forgotPasswordState = forgotPasswordState
    var userCodeDeliveryDetails = userCodeDeliveryDetails
}