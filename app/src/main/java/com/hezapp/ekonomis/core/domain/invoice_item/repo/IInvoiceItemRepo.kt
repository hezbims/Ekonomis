package com.hezapp.ekonomis.core.domain.invoice_item.repo

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction

interface IInvoiceItemRepo {
    suspend fun createOrUpdateInvoiceItems(invoiceItems: List<InvoiceItemEntity>)
    suspend fun deleteInvoiceItems(invoiceItems: List<InvoiceItemEntity>)
    suspend fun deleteInvoiceItemsByInvoiceId(invoiceId : Int)
    suspend fun getProductTransactions(
        startPeriod: Long,
        endPeriod: Long,
        productId: Int,
        transactionType: TransactionType,
    ) : List<ProductTransaction>
}