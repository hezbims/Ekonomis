package com.hezapp.ekonomis.core.transaction.data.repo

import com.hezapp.ekonomis.core.data.installment.dao.InstallmentDao
import com.hezapp.ekonomis.core.data.installment_item.dao.InstallmentItemDao
import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.data.invoice_item.dao.InvoiceItemDao
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails
import com.hezapp.ekonomis.core.transaction.data.mapper.getRoomInvoiceItemEntities
import com.hezapp.ekonomis.core.transaction.data.mapper.toRoomInvoiceEntity
import com.hezapp.ekonomis.core.transaction.domain.entity.TransactionEntity
import com.hezapp.ekonomis.core.transaction.domain.repo.ITransactionRepository
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import com.hezapp.ekonomis.core.transaction.data.mapper.toRoomInstallmentEntity
import com.hezapp.ekonomis.core.transaction.data.mapper.toRoomInstallmentItemEntity

class TransactionRepository(
    private val invoiceDao: InvoiceDao,
    private val invoiceItemDao: InvoiceItemDao,
    private val installmentDao: InstallmentDao,
    private val installmentItemDao: InstallmentItemDao,
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

            installmentDao.deleteByInvoiceId(invoiceId)

            dto.installment?.let {
                val installmentId = installmentDao.insert(it.toRoomInstallmentEntity(invoiceId))

                val itemsRoomEntity = it.items.map { item ->
                    item.toRoomInstallmentItemEntity(installmentId.toInt())
                }

                installmentItemDao.insert(itemsRoomEntity)
            }
        }
    }

    override suspend fun delete(invoiceId: Int) {
        invoiceDao.deleteInvoice(invoiceId)
    }

    override suspend fun getFullInvoiceDetails(id: Int): FullInvoiceDetails {
        return invoiceDao.getFullInvoiceDetails(id)
    }
}