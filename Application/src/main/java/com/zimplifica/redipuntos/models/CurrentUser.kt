package com.zimplifica.redipuntos.models

import android.util.Log
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.redipuntos.libs.utils.UserConfirmationStatus
import com.zimplifica.redipuntos.libs.utils.ValidationService

object CurrentUser{
    private var user : UserInformationResult? = null
    fun setCurrentUser(user : UserInformationResult){
        Log.e("CurrentUser",user.userPhoneNumber)
        this.user = user
    }
    fun getCurrentUser() : UserInformationResult?{
        return this.user
    }

    fun userConfirmationStatus() : UserConfirmationStatus? {
        return if(user==null){
            null
        }else{
            ValidationService.validateUserStatus(user!!)
        }
    }
}