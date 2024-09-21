package com.hezapp.ekonomis.core.data.invoice_item

import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import kotlinx.coroutines.delay

class FakeInvoiceItemRepo : IInvoiceItemRepo {
    override suspend fun createOrUpdateInvoiceItems(invoiceItems: List<InvoiceItemEntity>) {
        delay(300L)
        for (newInvoiceItem in invoiceItems){
            val oldItem = listItem.singleOrNull { dbItem ->
                dbItem.id == newInvoiceItem.id
            }

            if (oldItem == null)
                listItem.add(newInvoiceItem.copy(id = id++))
            else
                listItem.replaceAll { dbItem ->
                    if (dbItem.id == oldItem.id)
                        newInvoiceItem
                    else
                        dbItem
                }
        }
    }

    override suspend fun deleteInvoiceItems(invoiceItems: List<InvoiceItemEntity>) {
        delay(50L)
        invoiceItems.forEach { deleteItem ->
            listItem.removeIf {
                it.id == deleteItem.id
            }
        }
    }

    override suspend fun deleteInvoiceItems(invoiceId: Int) {
        listItem.removeIf { it.invoiceId == invoiceId }
    }

    companion object {
        val listItem = mutableListOf<InvoiceItemEntity>()
        var id = listItem.size + 1
    }
}