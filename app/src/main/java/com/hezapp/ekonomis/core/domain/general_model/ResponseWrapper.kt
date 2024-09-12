package com.hezapp.ekonomis.core.domain.general_model

sealed class ResponseWrapper<T , E : MyBasicError> {
    class Succeed<T , E : MyBasicError>(val data : T) : ResponseWrapper<T , E>()
    class Failed<T , E : MyBasicError>(val error : E? = null) : ResponseWrapper<T , E>()
    class Loading<T , E : MyBasicError> : ResponseWrapper<T, E>()

    fun isSucceed() : Boolean = this is Succeed
    fun isFailed() : Boolean = this is Failed
    fun isLoading() : Boolean = this is Loading
}

interface MyBasicError