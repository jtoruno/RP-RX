package com.zimplifica.redipuntos

import android.app.Application
import android.content.Context

class RPApplication : Application(){
    init {
        instance = this
    }

    companion object {
        private var instance: RPApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}


