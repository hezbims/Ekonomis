package com.hezapp.ekonomis.core.data.product

import com.hezapp.ekonomis.core.data.invoice.FakeInvoiceRepo
import com.hezapp.ekonomis.core.data.invoice_item.FakeInvoiceItemRepo
import com.hezapp.ekonomis.core.data.profile.FakeProfileRepo
import com.hezapp.ekonomis.core.domain.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.IProductRepo
import com.hezapp.ekonomis.core.domain.product.InsertProductError
import com.hezapp.ekonomis.core.domain.product.PreviewProductSummary
import com.hezapp.ekonomis.core.domain.product.ProductDetail
import com.hezapp.ekonomis.core.domain.product.ProductTransaction
import com.hezapp.ekonomis.core.domain.utils.PriceUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt

class FakeProductRepo : IProductRepo {
    override fun getAllProduct(searchQuery: String): Flow<ResponseWrapper<List<ProductEntity>, MyBasicError>> =
        flow {
            emit(ResponseWrapper.Loading())
            delay(200L)
            emit(ResponseWrapper.Succeed(
                listProduct.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }
            ))
        }

    override fun insertProduct(newProduct: ProductEntity): Flow<ResponseWrapper<Any?, InsertProductError>> =
        flow {
            emit(ResponseWrapper.Loading())
            delay(50L)
            if (newProduct.name.isEmpty()){
                emit(ResponseWrapper.Failed(InsertProductError.EmptyInputName))
            } else if(
                listProduct.firstOrNull { it.name.equals(newProduct.name, ignoreCase = true) } != null
            ) {
               emit(ResponseWrapper.Failed(InsertProductError.AlreadyUsed))
            }
            else {
                listProduct.add(newProduct.copy(id = id++))
                emit(ResponseWrapper.Succeed(null))
            }
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

    override suspend fun getProductDetail(productId : Int): ProductDetail {
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
        }

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
        }
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
        val listProduct = mutableListOf(
            ProductEntity(
                id = 1,
                name = "Tuna Deho"
            ),
            ProductEntity(
                id = 2,
                name = "White Heinz Vinegar"
            )
        )
        var id = listProduct.size + 1
    }
}