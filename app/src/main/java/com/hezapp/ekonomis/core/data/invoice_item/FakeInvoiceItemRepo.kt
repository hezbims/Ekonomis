package com.hezapp.ekonomis.core.data.invoice_item

import com.hezapp.ekonomis.core.domain.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import kotlinx.coroutines.delay

class FakeInvoiceItemRepo : IInvoiceItemRepo {
    override suspend fun createOrUpdateInvoiceItems(invoiceItems: List<InvoiceItemEntity>) {
        delay(300L)
        for (newInvoiceItem in invoiceItems){
            val oldItem = listItem.singleOrNull {
                it.id == newInvoiceItem.id
            }

            if (oldItem == null)
                listItem.add(newInvoiceItem.copy(id = id++))
            else
                listItem.replaceAll {
                    if (it.id == oldItem.id)
                        newInvoiceItem
                    else
                        it
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

    companion object {
        val listItem = mutableListOf<InvoiceItemEntity>()
        var id = listItem.size + 1
    }
}