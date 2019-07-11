package com.zimplifica.awsplatform.Utils

import android.util.Log

object PlatformUtils {
    fun encrypt(data : String) : String?{
        val key = System.getenv()
        Log.e("Keys",key.toString())
        return "null"
    }
}