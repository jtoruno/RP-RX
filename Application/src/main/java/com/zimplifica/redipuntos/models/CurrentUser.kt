package com.zimplifica.redipuntos.models

import android.util.Log
import com.zimplifica.domain.entities.UserInformationResult

object CurrentUser{
    private var user : UserInformationResult? = null
    fun setCurrentUser(user : UserInformationResult){
        Log.e("CurrentUser",user.userPhoneNumber)
        this.user = user
    }
    fun getCurrentUser() : UserInformationResult?{
        return this.user
    }
}