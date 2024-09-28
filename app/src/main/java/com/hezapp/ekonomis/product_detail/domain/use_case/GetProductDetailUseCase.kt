package com.hezapp.ekonomis.product_detail.domain.use_case

import android.util.Log
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.monthly_stock.repo.IMonthlyStockRepo
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.domain.product.model.TransactionSummary
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.core.domain.utils.getPreviousMonthYear
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetProductDetailUseCase(
    private val productRepo : IProductRepo,
    private val invoiceItemRepo: IInvoiceItemRepo,
    private val monthlyStockRepo: IMonthlyStockRepo,
    private val transactionProvider : ITransactionProvider,
) {
    operator fun invoke(
        productId : Int,
        monthYearPeriod: Long,
    ) : Flow<ResponseWrapper<ProductDetail, MyBasicError>> =
    flow<ResponseWrapper<ProductDetail, MyBasicError>> {
        emit(ResponseWrapper.Loading())

        val startMonthPeriod = monthYearPeriod.toCalendar().toBeginningOfMonth().timeInMillis
        val prevMonthPeriod = startMonthPeriod.getPreviousMonthYear()

        val productDetail = transactionProvider.withTransaction {
            val currentProduct = productRepo.getProduct(productId = productId)
            var currentTransactionSummary = getTransactionSummaryOfAMonth(
                startPeriod = startMonthPeriod,
                productId = productId,
            )

            if (currentTransactionSummary.firstDayOfMonthStock == null) {
                val prevMonthTransactionSummary = getTransactionSummaryOfAMonth(
                    startPeriod = prevMonthPeriod,
                    productId = productId
                )

                monthlyStockRepo.upsertMonthlyStock(
                    MonthlyStockEntity(
                        id = 0,
                        monthYearPeriod = startMonthPeriod,
                        productId = productId,
                        quantityPerUnitType = prevMonthTransactionSummary.latestDayOfMonthStock,
                    )
                )

                currentTransactionSummary = TransactionSummary(
                    outProductTransactions = currentTransactionSummary.outProductTransactions,
                    inProductTransactions = currentTransactionSummary.inProductTransactions,
                    firstDayOfMonthStock = prevMonthTransactionSummary.latestDayOfMonthStock,
                )
            }
            return@withTransaction ProductDetail(
                id = currentProduct.id,
                inProductTransactions = currentTransactionSummary.inProductTransactions,
                outProductTransactions = currentTransactionSummary.outProductTransactions,
                productName = currentProduct.name,
                firstDayOfMonthStock = currentTransactionSummary.firstDayOfMonthStock!!,
            )
        }

        emit(ResponseWrapper.Succeed(productDetail))
    }.catch {
        Log.e("qqq Get Product Detail Use Case Err", "${it.message}")
        emit(ResponseWrapper.Failed())
    }

    private suspend fun getTransactionSummaryOfAMonth(
        startPeriod: Long,
        productId: Int,
    ) : TransactionSummary {
        val nextMonth = startPeriod.getNextMonthYear()

        val outTransactions = invoiceItemRepo.getProductTransactions(
            productId = productId,
            startPeriod = startPeriod,
            endPeriod = nextMonth,
            transactionType = TransactionType.PENJUALAN
        )

        val inTransactions = invoiceItemRepo.getProductTransactions(
            productId = productId,
            startPeriod = startPeriod,
            endPeriod = nextMonth,
            transactionType = TransactionType.PEMBELIAN
        )
        val currentMonthStock = monthlyStockRepo.getMonthlyStock(
            startMonthPeriod = startPeriod,
            productId = productId,
        )?.quantityPerUnitType

        return TransactionSummary(
            inProductTransactions = inTransactions,
            outProductTransactions = outTransactions,
            firstDayOfMonthStock = currentMonthStock,
        )
    }
}