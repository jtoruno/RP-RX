package com.zimplifica.redipuntos.libs.utils

import android.app.Activity
import android.content.Context

object SharedPreferencesUtils {
    fun saveStringInSp(ctx: Context, key: String, value: String) {
        val editor = ctx.getSharedPreferences("SP", Activity.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringFromSp(ctx: Context, key: String): String? {
        val sharedPreferences = ctx.getSharedPreferences("SP", Activity.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    fun saveBooleanInSp(ctx: Context, key: String, state: Boolean){
        val editor = ctx.getSharedPreferences("SP", Activity.MODE_PRIVATE).edit()
        editor.putBoolean(key, state)
        editor.apply()
    }

    fun getBooleanInSp(ctx: Context, key: String):Boolean{
        val sharedPreferences = ctx.getSharedPreferences("SP", Activity.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, false)
    }
}