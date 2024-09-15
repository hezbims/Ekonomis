package com.hezapp.ekonomis.core.data.invoice

import com.hezapp.ekonomis.add_new_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.core.data.invoice_item.FakeInvoiceItemRepo
import com.hezapp.ekonomis.core.data.profile.FakeProfileRepo
import com.hezapp.ekonomis.core.domain.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo
import kotlinx.coroutines.delay

class FakeInvoiceRepo : IInvoiceRepo {
    override suspend fun createNewInvoice(newInvoice: InvoiceFormModel) : Int {
        delay(500L)
        listData.add(newInvoice.toEntity().copy(id = id++))
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

    override suspend fun getPreviewInvoices(): List<PreviewTransactionHistory> {
        delay(300L)

        val result =listData.map { invoice ->
            val currentInvoiceItem = FakeInvoiceItemRepo.listItem.filter {
                it.invoiceId == invoice.id
            }

            val currentProfile = FakeProfileRepo.listPerson.single { it.id == invoice.profileId }

            PreviewTransactionHistory(
                date = invoice.date,
                id = invoice.id,
                profileName = currentProfile.name,
                profileType = currentProfile.type,
                totalPrice = currentInvoiceItem.sumOf { it.price.toLong() },
            )
        }
        return result
    }

    companion object {
        val listData = mutableListOf<InvoiceEntity>()
        var id = listData.size + 1
    }
}