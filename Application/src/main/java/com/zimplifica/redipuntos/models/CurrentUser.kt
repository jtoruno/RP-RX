package com.zimplifica.redipuntos.models

import com.zimplifica.domain.entities.UserInformationResult

object CurrentUser{
    private var user : UserInformationResult? = null
    fun setCurrentUser(user : UserInformationResult){
        this.user = user
    }
    fun getCurrentUser() : UserInformationResult?{
        return this.user
    }
}