package com.hezapp.ekonomis.core.domain.invoice.repo

import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails

interface IInvoiceRepo {
    suspend fun createOrUpdateInvoice(newInvoice : InvoiceFormModel) : Int
    suspend fun getFullInvoiceDetails(id : Int) : FullInvoiceDetails
    suspend fun deleteInvoice(id: Int)
}