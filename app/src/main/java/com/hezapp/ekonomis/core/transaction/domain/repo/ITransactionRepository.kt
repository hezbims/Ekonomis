package com.hezapp.ekonomis.core.transaction.domain.repo

import com.hezapp.ekonomis.core.transaction.domain.entity.TransactionEntity

interface ITransactionRepository {
    suspend fun saveInvoice(dto: TransactionEntity)
}