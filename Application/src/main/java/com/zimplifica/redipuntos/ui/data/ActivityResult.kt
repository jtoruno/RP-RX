package com.zimplifica.redipuntos.ui.data

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import auto.parcel.AutoParcel
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable

@AutoParcel
abstract class ActivityResult : Parcelable {

    val isCanceled: Boolean
        get() = resultCode() == Activity.RESULT_CANCELED

    val isOk: Boolean
        get() = resultCode() == Activity.RESULT_OK

    abstract fun requestCode(): Int
    abstract fun resultCode(): Int
    @Nullable
    abstract fun intent(): Intent

    @AutoParcel.Builder
    abstract class Builder {
        abstract fun requestCode(code: Int): Builder
        abstract fun resultCode(code: Int): Builder
        abstract fun intent(code: Intent): Builder
        abstract fun build(): ActivityResult
    }

    abstract fun toBuilder(): Builder

    fun isRequestCode(v: Int): Boolean {
        return requestCode() == v
    }

    companion object {

        @NonNull
        fun create(requestCode: Int, resultCode: Int, @Nullable intent: Intent): ActivityResult {
            return ActivityResult.builder()
                .requestCode(requestCode)
                .resultCode(resultCode)
                .intent(intent)
                .build()
        }

        fun builder(): Builder {
            return AutoParcel_ActivityResult.Builder()
        }
    }
}