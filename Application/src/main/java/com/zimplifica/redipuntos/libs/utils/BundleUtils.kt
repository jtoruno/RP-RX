package com.zimplifica.redipuntos.libs.utils

import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.Nullable


object BundleUtils {

    fun maybeGetBundle(@Nullable state: Bundle?, @NonNull key: String): Bundle? {
        return state?.getBundle(key)

    }
}