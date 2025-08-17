package com.hezapp.ekonomis.core.data.transaction.repo

import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.data.invoice_item.dao.InvoiceItemDao
import com.hezapp.ekonomis.core.data.transaction.mapper.getRoomInvoiceItemEntities
import com.hezapp.ekonomis.core.data.transaction.mapper.toRoomInvoiceEntity
import com.hezapp.ekonomis.core.domain.transaction.entity.TransactionEntity
import com.hezapp.ekonomis.core.domain.transaction.repo.ITransactionRepository
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider

class TransactionRepository(
    private val invoiceDao: InvoiceDao,
    private val invoiceItemDao: InvoiceItemDao,
    private val transactionProvider: ITransactionProvider,
) : ITransactionRepository {
    override suspend fun saveInvoice(dto: TransactionEntity) {
        transactionProvider.withTransaction {
            var invoiceId = invoiceDao.upsertInvoice(dto.toRoomInvoiceEntity())
                .toInt()
            if (invoiceId == -1)
                invoiceId = dto.id

            invoiceItemDao.deleteInvoiceItemsByInvoiceId(invoiceId)

            invoiceItemDao.upsertInvoiceItems(
                *dto.getRoomInvoiceItemEntities(invoiceId).toTypedArray())
        }
    }
}