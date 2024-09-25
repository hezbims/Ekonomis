package com.hezapp.ekonomis.core.data.database

import androidx.room.withTransaction
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider

class TransactionProvider(
    private val db : EkonomisDatabase
) : ITransactionProvider {
    override suspend fun <R> withTransaction(block: suspend () -> R) {
        db.withTransaction {
            block()
        }
    }
}