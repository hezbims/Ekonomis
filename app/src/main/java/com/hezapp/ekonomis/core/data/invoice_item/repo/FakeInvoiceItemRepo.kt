package com.hezapp.ekonomis.core.data.invoice_item.repo

import com.hezapp.ekonomis.core.data.invoice.repo.FakeInvoiceRepo
import com.hezapp.ekonomis.core.data.profile.repo.FakeProfileRepo
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction
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

    override suspend fun deleteInvoiceItemsByInvoiceId(invoiceId: Int) {
        listItem.removeIf { it.invoiceId == invoiceId }
    }

    override suspend fun getProductTransactions(
        startPeriod: Long,
        endPeriod: Long,
        productId: Int,
        transactionType: TransactionType
    ): List<ProductTransaction> {
        return listItem.mapNotNull { invoiceItem ->
            if (invoiceItem.productId != productId){
                return@mapNotNull null
            }

            val invoice = FakeInvoiceRepo.listData.single { invoice ->
                invoice.id == invoiceItem.invoiceId
            }

            if (invoice.date < startPeriod || invoice.date >= endPeriod){
                return@mapNotNull null
            }

            val profile = FakeProfileRepo.listPerson.single { profile ->
                invoice.profileId == profile.id
            }

            ProductTransaction(
                date = invoice.date,
                id = invoiceItem.id,
                ppn = invoice.ppn,
                price = invoiceItem.price,
                quantity = invoiceItem.quantity,
                unitType = invoiceItem.unitType,
                profileName = profile.name
            )
        }
    }

    companion object {
        val listItem = mutableListOf<InvoiceItemEntity>()
        var id = listItem.size + 1
    }
}