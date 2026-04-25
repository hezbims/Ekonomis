package com.hezapp.ekonomis.core.domain.general_model

sealed class ResponseWrapper<T , E> {
    class Succeed<T , E>(val data : T) : ResponseWrapper<T , E>()
    class Failed<T , E>(val error : E? = null) : ResponseWrapper<T , E>()
    class Loading<T , E> : ResponseWrapper<T, E>()

    fun isSucceed() : Boolean = this is Succeed
    fun isFailed() : Boolean = this is Failed
    fun isLoading() : Boolean = this is Loading
    fun asSucceed(): Succeed<T , E> =
        this as Succeed
}

interface MyBasicError