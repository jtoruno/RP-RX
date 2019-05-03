package com.zimplifica.domain.entities


sealed class Result<out ResultValue, out E>{
    data class sucess<out ResultValue>(val result : ResultValue) : Result<ResultValue, Nothing>()
    data class error<out E>(val Error : E) : Result<Nothing,E>()
}


/*
public enum Result<ResultValue: Equatable>: Equatable{
    case success(ResultValue)
    case error(Error)

    public static func == (lhs: Result<ResultValue>, rhs: Result<ResultValue>) -> Bool {
        return true
    }
}*/