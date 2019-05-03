package com.zimplifica.redipuntos.libs

import android.os.Parcelable
import auto.parcel.AutoParcel
import com.zimplifica.domain.useCases.AuthenticationUseCase


@AutoParcel
abstract class Environment : Parcelable {

    abstract fun webEndpoint(): String
    abstract fun authenticationUseCase(): AuthenticationUseCase

    @AutoParcel.Builder
    abstract class Builder {
        abstract fun webEndpoint(endPoint: String): Builder
        abstract fun authenticationUseCase(authenticationUseCase: AuthenticationUseCase) : Builder
        abstract fun build(): Environment
    }

    abstract fun toBuilder(): Builder

    companion object {

        fun builder(): Builder {
            return AutoParcel_Environment.Builder()
        }
    }
}