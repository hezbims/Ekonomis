package com.hezapp.ekonomis.core.data.invoice

import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.core.data.invoice_item.FakeInvoiceItemRepo
import com.hezapp.ekonomis.core.data.product.repo.FakeProductRepo
import com.hezapp.ekonomis.core.data.profile.repo.FakeProfileRepo
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails
import com.hezapp.ekonomis.core.domain.invoice.relationship.InvoiceWithInvoiceItemAndProducts
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo
import com.hezapp.ekonomis.core.domain.invoice_item.relationship.InvoiceItemWithProduct
import com.hezapp.ekonomis.core.domain.utils.isInAMonthYearPeriod
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

    override suspend fun getPreviewInvoices(
        filter: PreviewTransactionFilter,
    ): List<PreviewTransactionHistory> {
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
        }.filter {
            it.date.isInAMonthYearPeriod(monthYearPeriod = filter.monthYear)
        }.sortedWith(
            compareBy({ -it.date } , { -it.id })
        )
        return result
    }

    override suspend fun getFullInvoiceDetails(id: Int): FullInvoiceDetails {
        val invoice = listData.single {
            it.id == id
        }

        val profile = FakeProfileRepo.listPerson.single {
            it.id == invoice.profileId
        }

        val invoiceItems = FakeInvoiceItemRepo.listItem.filter {
            it.invoiceId == invoice.id
        }.map { invoiceItem ->
            val product = FakeProductRepo.listProduct.single { product ->
                invoiceItem.productId == product.id
            }
            InvoiceItemWithProduct(
                invoiceItem = invoiceItem,
                product = product
            )
        }

        return FullInvoiceDetails(
            profile = profile,
            invoice = InvoiceWithInvoiceItemAndProducts(
                invoice = invoice,
                invoiceItemWithProducts = invoiceItems
            )
        )
    }

    override suspend fun deleteInvoice(id: Int) {
        listData.removeIf { it.id == id }
    }

    companion object {
        val listData = mutableListOf<InvoiceEntity>()
        var id = listData.size + 1
    }
}