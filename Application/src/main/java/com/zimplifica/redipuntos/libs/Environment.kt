package com.zimplifica.redipuntos.libs

import android.os.Parcelable
import auto.parcel.AutoParcel


@AutoParcel
abstract class Environment : Parcelable {

    abstract fun webEndpoint(): String

    @AutoParcel.Builder
    abstract class Builder {
        abstract fun webEndpoint(endPoint: String): Builder
        abstract fun build(): Environment
    }

    abstract fun toBuilder(): Builder

    companion object {

        fun builder(): Builder {
            return AutoParcel_Environment.Builder()
        }
    }
}