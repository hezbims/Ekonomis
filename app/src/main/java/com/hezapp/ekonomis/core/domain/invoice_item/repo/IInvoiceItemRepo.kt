package com.hezapp.ekonomis.core.domain.invoice_item.repo

import com.hezapp.ekonomis.core.domain.entity.InvoiceItemEntity

interface IInvoiceItemRepo {
    suspend fun createOrUpdateInvoiceItems(invoiceItems: List<InvoiceItemEntity>)
    suspend fun deleteInvoiceItems(invoiceItems: List<InvoiceItemEntity>)
}