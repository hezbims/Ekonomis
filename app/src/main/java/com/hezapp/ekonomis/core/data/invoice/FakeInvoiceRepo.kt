package com.hezapp.ekonomis.core.data.invoice

import com.hezapp.ekonomis.core.domain.entity.InvoiceEntity
import com.hezapp.ekonomis.add_new_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo
import kotlinx.coroutines.delay

class FakeInvoiceRepo : IInvoiceRepo {
    override suspend fun createNewInvoice(newInvoice: InvoiceFormModel) : Int {
        delay(500L)
        listData.add(newInvoice.toEntity().copy(id = listData.size))
        return listData.last().id
    }

    override suspend fun updateInvoice(newInvoice: InvoiceFormModel) : Int {
        listData.replaceAll { oldInvoice ->
            if (oldInvoice.id == newInvoice.id)
                newInvoice.toEntity()
            else
                oldInvoice
        }
        return newInvoice.id
    }

    companion object {
        val listData = mutableListOf<InvoiceEntity>()
    }
}