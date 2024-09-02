package com.hezapp.ekonomis.core.domain.model

sealed class ResponseWrapper<T , E : MyBasicError> {
    class Succeed<T>(val data : T) : ResponseWrapper<T , MyBasicError>()
    class Failed<T>(val error : MyBasicError? = null) : ResponseWrapper<T , MyBasicError>()
    class Loading<T> : ResponseWrapper<T, MyBasicError>()
}

interface MyBasicError