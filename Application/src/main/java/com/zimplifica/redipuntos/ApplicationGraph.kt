package com.zimplifica.redipuntos

import com.zimplifica.redipuntos.libs.Environment


interface ApplicationGraph {
    fun environment(): Environment
    fun inject(app: RPApplication)
}


