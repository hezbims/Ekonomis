package com.hezapp.ekonomis.core.domain.invoice.repo

import com.hezapp.ekonomis.add_new_transaction.domain.model.InvoiceFormModel

interface IInvoiceRepo {
    suspend fun createNewInvoice(newInvoice : InvoiceFormModel) : Int
    suspend fun updateInvoice(newInvoice: InvoiceFormModel) : Int
}