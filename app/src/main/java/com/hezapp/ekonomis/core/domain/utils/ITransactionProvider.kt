package com.hezapp.ekonomis.core.domain.utils

interface ITransactionProvider {
    suspend fun <R> withTransaction(block: suspend () -> R)
}