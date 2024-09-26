package com.hezapp.ekonomis.core.data.invoice_item.repo

import com.hezapp.ekonomis.core.data.invoice_item.dao.InvoiceItemDao
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction

class InvoiceItemRepo(
    private val dao: InvoiceItemDao
) : IInvoiceItemRepo {

    override suspend fun createOrUpdateInvoiceItems(invoiceItems: List<InvoiceItemEntity>) {
        dao.upsertInvoiceItems(*invoiceItems.toTypedArray())
    }

    override suspend fun deleteInvoiceItems(invoiceItems: List<InvoiceItemEntity>) {
        dao.deleteInvoiceItems(*invoiceItems.toTypedArray())
    }

    override suspend fun deleteInvoiceItemsByInvoiceId(invoiceId: Int) {
        dao.deleteInvoiceItemsByInvoiceId(invoiceId)
    }

    override suspend fun getProductTransactions(
        startPeriod: Long,
        endPeriod: Long,
        productId: Int,
        transactionType: TransactionType
    ): List<ProductTransaction> {
        return dao.getListProductTransactions(
            firstDayOfPeriod = startPeriod,
            lastDayOfPeriod = endPeriod,
            productId = productId,
            transactionTypeId = transactionType.id,
        )
    }
}