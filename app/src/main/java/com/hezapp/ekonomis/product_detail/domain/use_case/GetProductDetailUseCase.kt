package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.monthly_stock.repo.IMonthlyStockRepo
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import com.hezapp.ekonomis.core.domain.utils.getPreviousMonthYear
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetProductDetailUseCase(
    private val productRepo : IProductRepo,
    private val monthlyStockRepo: IMonthlyStockRepo,
    private val transactionProvider : ITransactionProvider,
    private val getTransactionSummaryOfAMonth: GetTransactionSummaryOfAMonthUseCase,
    private val reportingService: IErrorReportingService,
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

                val monthlyStockId = monthlyStockRepo.upsertMonthlyStock(
                    MonthlyStockEntity(
                        id = 0,
                        monthYearPeriod = startMonthPeriod,
                        productId = productId,
                        quantityPerUnitType = prevMonthTransactionSummary.latestDayOfMonthStock,
                    )
                )
                assert(monthlyStockId != -1)

                currentTransactionSummary = currentTransactionSummary.copy(
                    firstDayOfMonthStock = prevMonthTransactionSummary.latestDayOfMonthStock,
                    monthlyStockId = monthlyStockId,
                )
            }
            return@withTransaction ProductDetail(
                id = currentProduct.id,
                productName = currentProduct.name,
                transactionSummary = currentTransactionSummary,
            )
        }

        emit(ResponseWrapper.Succeed(productDetail))
    }.catch { t ->
        reportingService.logNonFatalError(t, mapOf(
            "productId" to productId,
            "monthYearPeriod" to monthYearPeriod,
        ))
        emit(ResponseWrapper.Failed())
    }
}