package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.getPreviousMonthYear
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar
import com.hezapp.ekonomis.product_detail.application.dto.ProductQuantityDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetLatestPreviousMonthStock(
    private val getTransactionSummaryOfAMonth: GetTransactionSummaryOfAMonthUseCase,
    private val reportingService: IErrorReportingService,
    private val timeService: ITimeService,
) {
    operator  fun invoke(
        currentMonthPeriod: Long,
        productId: Int,
    ) : Flow<ResponseWrapper<ProductQuantityDto , MyBasicError>> =
    flow<ResponseWrapper<ProductQuantityDto, MyBasicError>>{
        emit(ResponseWrapper.Loading())

        val previousMonthPeriod = currentMonthPeriod
            .toCalendar(timeService)
            .toBeginningOfMonth(timeService)
            .timeInMillis
            .getPreviousMonthYear(timeService)

        val queryResult = getTransactionSummaryOfAMonth(
            startPeriod = previousMonthPeriod,
            productId = productId,
        )
        val latestStockFromPreviousMonth = queryResult.latestDayOfMonthStock

        emit(ResponseWrapper.Succeed(data = latestStockFromPreviousMonth))

    }.catch { t ->
        reportingService.logNonFatalError(t, mapOf(
            "currentMonthPeriod" to currentMonthPeriod,
            "productId" to productId,
        ))
        emit(ResponseWrapper.Failed())
    }
}