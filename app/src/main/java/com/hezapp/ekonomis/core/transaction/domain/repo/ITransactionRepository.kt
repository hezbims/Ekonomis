package com.hezapp.ekonomis.core.transaction.domain.repo

import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails
import com.hezapp.ekonomis.core.transaction.domain.entity.TransactionEntity

interface ITransactionRepository {
    suspend fun saveInvoice(dto: TransactionEntity)
    suspend fun delete(invoiceId: Int)
    suspend fun getFullInvoiceDetails(id: Int) : FullInvoiceDetails
    suspend fun getPreviewInvoices(
        filter: PreviewTransactionFilter,
    ) : List<PreviewTransactionHistory>
}