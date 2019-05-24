package com.zimplifica.redipuntos.libs

import android.os.Parcelable
import auto.parcel.AutoParcel
import android.content.SharedPreferences
import com.zimplifica.redipuntos.models.CurrentUser
import com.zimplifica.redipuntos.services.AuthenticationService


@AutoParcel
abstract class Environment : Parcelable {

    abstract fun webEndpoint(): String
    abstract fun authenticationUseCase(): AuthenticationService
    abstract fun sharedPreferences(): SharedPreferences
    abstract fun currentUser(): CurrentUser
    @AutoParcel.Builder
    abstract class Builder {
        abstract fun webEndpoint(endPoint: String): Builder
        abstract fun authenticationUseCase(authenticationUseCase: AuthenticationService) : Builder
        abstract fun sharedPreferences(sharedPreferences: SharedPreferences): Builder
        abstract fun currentUser(currentUser: CurrentUser): Builder
        abstract fun build(): Environment
    }

    abstract fun toBuilder(): Builder

    companion object {

        fun builder(): Builder {
            return AutoParcel_Environment.Builder()
        }
    }
}