package com.zimplifica.domain.entities

import java.lang.Exception

/*
sealed class Result<out ResultValue, out E>{
    data class sucess<out ResultValue>(val result : ResultValue) : Result<ResultValue, Nothing>()
    data class error<out E>(val Error : E) : Result<Nothing,E>()
}
*/

/*
public enum Result<ResultValue: Equatable>: Equatable{
    case success(ResultValue)
    case error(Error)

    public static func == (lhs: Result<ResultValue>, rhs: Result<ResultValue>) -> Bool {
        return true
    }
}*/

sealed class Result<out T : Any> {
    data class success<out T : Any>(val value: T? = null) : Result<T>()
    data class failure(val cause: Exception? = null) : Result<Nothing>()

    fun isFail(): Boolean = when(this){
        is success -> false
        is failure -> true
    }
}