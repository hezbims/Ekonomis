package com.hezapp.ekonomis.core.data.product.repo

import com.hezapp.ekonomis.BuildConfig
import com.hezapp.ekonomis.core.data.invoice.repo.FakeInvoiceRepo
import com.hezapp.ekonomis.core.data.invoice_item.repo.FakeInvoiceItemRepo
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo

class FakeProductRepo : IProductRepo {
    override suspend fun getAllProduct(searchQuery: String): List<ProductEntity> {
        return listProduct.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    override  suspend fun insertProduct(newProduct: ProductEntity) {
        listProduct.add(newProduct.copy(id = id++))
    }

    override suspend fun getPreviewProductSummaries(
        searchQuery: String,
    ): List<PreviewProductSummary> {
        return listProduct.map { product ->

            val invoiceItemsOut = FakeInvoiceItemRepo.listItem.mapNotNull { invoiceItem ->
                val invoice = FakeInvoiceRepo.listData.single { invoice ->
                    invoiceItem.invoiceId == invoice.id
                }
                if (
                    invoiceItem.productId != product.id ||
                    invoice.transactionType != TransactionType.PEMBELIAN
                )
                    null
                else {
                    InvoiceItemWithInvoice(
                        invoiceItem = invoiceItem,
                        invoice = invoice,
                    )
                }
            }

            val hargaPokokItem = invoiceItemsOut.maxByOrNull { it.invoice.date }

            PreviewProductSummary(
                id = product.id,
                name = product.name,
                ppn = hargaPokokItem?.invoice?.ppn,
                price = hargaPokokItem?.invoiceItem?.price,
                quantity = hargaPokokItem?.invoiceItem?.quantity,
                unitType = hargaPokokItem?.invoiceItem?.unitType,
            )
        }.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    override suspend fun getProduct(productId: Int): ProductEntity {
        return listProduct.single { it.id == productId }
    }

    private data class InvoiceItemWithInvoice(
        val invoiceItem : InvoiceItemEntity,
        val invoice: InvoiceEntity,
    )



    companion object {
        val listProduct = if (BuildConfig.DEBUG) mutableListOf(
            ProductEntity(
                id = 1,
                name = "Tuna Deho"
            ),
            ProductEntity(
                id = 2,
                name = "White Heinz Vinegar"
            )
        ) else mutableListOf()
        var id = listProduct.size + 1
    }
}