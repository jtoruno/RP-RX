package com.zimplifica.redipuntos.libs.utils

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable


object BundleUtils {

    fun maybeGetBundle(@Nullable state: Bundle?, @NonNull key: String): Bundle? {
        return state?.getBundle(key)

    }
}