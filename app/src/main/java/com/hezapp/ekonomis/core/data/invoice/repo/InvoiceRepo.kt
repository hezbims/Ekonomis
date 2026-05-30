package com.hezapp.ekonomis.core.data.invoice.repo

import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo

class InvoiceRepo(
    private val dao: InvoiceDao
) : IInvoiceRepo {
    override suspend fun createOrUpdateInvoice(newInvoice: InvoiceFormModel): Int {
        return dao.upsertInvoice(
            newInvoice.toEntity()
        ).toInt()
    }

    override suspend fun getFullInvoiceDetails(id: Int): FullInvoiceDetails {
        return dao.getFullInvoiceDetails(id = id)
    }

    override suspend fun deleteInvoice(id: Int) {
        dao.deleteInvoice(invoiceId = id)
    }
}