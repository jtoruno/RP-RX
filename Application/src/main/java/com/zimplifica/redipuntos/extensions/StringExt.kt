package com.zimplifica.redipuntos.extensions

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }