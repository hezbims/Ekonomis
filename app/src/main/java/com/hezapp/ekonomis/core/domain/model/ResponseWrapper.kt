package com.hezapp.ekonomis.core.domain.model

sealed class ResponseWrapper<T , E : MyBasicError> {
    class Succeed<T , E : MyBasicError>(val data : T) : ResponseWrapper<T , E>()
    class Failed<T , E : MyBasicError>(val error : E? = null) : ResponseWrapper<T , E>()
    class Loading<T , E : MyBasicError> : ResponseWrapper<T, E>()
}

interface MyBasicError