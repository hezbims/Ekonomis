package com.hezapp.ekonomis.core.domain.transaction.repo

import com.hezapp.ekonomis.core.domain.transaction.entity.TransactionEntity

interface ITransactionRepository {
    suspend fun saveInvoice(dto: TransactionEntity)
}