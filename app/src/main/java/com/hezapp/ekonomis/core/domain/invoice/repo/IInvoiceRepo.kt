package com.hezapp.ekonomis.core.domain.invoice.repo

import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory

interface IInvoiceRepo {
    suspend fun createNewInvoice(newInvoice : InvoiceFormModel) : Int
    suspend fun updateInvoice(newInvoice: InvoiceFormModel) : Int
    suspend fun getPreviewInvoices(
        filter: PreviewTransactionFilter,
    ) : List<PreviewTransactionHistory>
}