package com.hezapp.ekonomis.core.data.product.repo

import com.hezapp.ekonomis.BuildConfig
import com.hezapp.ekonomis.core.data.invoice.FakeInvoiceRepo
import com.hezapp.ekonomis.core.data.invoice_item.FakeInvoiceItemRepo
import com.hezapp.ekonomis.core.data.profile.repo.FakeProfileRepo
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import com.hezapp.ekonomis.core.domain.utils.PriceUtils
import com.hezapp.ekonomis.core.domain.utils.isInAMonthYearPeriod
import kotlin.math.roundToInt

class FakeProductRepo : IProductRepo {
    override suspend fun getAllProduct(searchQuery: String): List<ProductEntity> {
        return listProduct.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    override  suspend fun insertProduct(newProduct: ProductEntity) {
        listProduct.add(newProduct.copy(id = id++))
    }

    override suspend fun getPreviewProductSummaries(): List<PreviewProductSummary> {
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
                costOfGoodsSold = hargaPokokItem?.let {
                    PriceUtils.getCostOfGoodsSoldUseCase(
                        totalPrice = it.invoiceItem.price,
                        quantity = it.invoiceItem.quantity,
                        ppn = it.invoice.ppn,
                    ).roundToInt()
                },
                unitType = hargaPokokItem?.invoiceItem?.unitType,
            )
        }
    }

    override suspend fun getProductDetail(
        productId : Int,
        monthYearPeriod: Long,
    ): ProductDetail {
        val product = listProduct.single { it.id == productId }
        val inProductTransactions = FakeInvoiceItemRepo.listItem.mapNotNull { invoiceItem ->
            val invoice = FakeInvoiceRepo.listData.single { invoice ->
                invoiceItem.invoiceId == invoice.id
            }
            val profile = FakeProfileRepo.listPerson.single { profile ->
                profile.id == invoice.profileId
            }

            // kalau pembelian, atau product id nya bukan ini,
            if (invoiceItem.productId != productId ||
                invoice.transactionType != TransactionType.PEMBELIAN)
                null
            else
                ProductTransaction(
                    profileName = profile.name,
                    date = invoice.date,
                    ppn = invoice.ppn,
                    quantity = invoiceItem.quantity,
                    totalPrice = invoiceItem.price,
                    unitType = invoiceItem.unitType,
                    id = invoiceItem.id,
                )
        }.filter { it.date.isInAMonthYearPeriod(monthYearPeriod) }

        val outProductTransactions = FakeInvoiceItemRepo.listItem.mapNotNull { invoiceItem ->
            val invoice = FakeInvoiceRepo.listData.single { invoice ->
                invoiceItem.invoiceId == invoice.id
            }
            val profile = FakeProfileRepo.listPerson.single { profile ->
                profile.id == invoice.profileId
            }

            // kalau pembelian, atau product id nya bukan ini,
            if (invoiceItem.productId != productId ||
                invoice.transactionType != TransactionType.PENJUALAN)
                null
            else
                ProductTransaction(
                    profileName = profile.name,
                    date = invoice.date,
                    ppn = invoice.ppn,
                    quantity = invoiceItem.quantity,
                    totalPrice = invoiceItem.price,
                    unitType = invoiceItem.unitType,
                    id = invoiceItem.id,
                )
        }.filter { it.date.isInAMonthYearPeriod(monthYearPeriod) }

        return ProductDetail(
            id = productId,
            productName = product.name,
            inProductTransactions = inProductTransactions,
            outProductTransactions = outProductTransactions,
        )
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