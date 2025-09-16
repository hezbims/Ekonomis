package com.hezapp.ekonomis.product_detail.domain.use_case

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.getPreviousMonthYear
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetLatestPreviousMonthStock(
    private val getTransactionSummaryOfAMonth: GetTransactionSummaryOfAMonthUseCase,
    private val reportingService: IErrorReportingService,
) {
    operator  fun invoke(
        currentMonthPeriod: Long,
        productId: Int,
    ) : Flow<ResponseWrapper<QuantityPerUnitType , MyBasicError>> =
    flow<ResponseWrapper<QuantityPerUnitType , MyBasicError>>{
        emit(ResponseWrapper.Loading())

        val previousMonthPeriod = currentMonthPeriod
            .toCalendar()
            .toBeginningOfMonth()
            .timeInMillis
            .getPreviousMonthYear()

        val previousMonthTransactionSummary = getTransactionSummaryOfAMonth(
            startPeriod = previousMonthPeriod,
            productId = productId,
        )

        emit(ResponseWrapper.Succeed(data = previousMonthTransactionSummary.latestDayOfMonthStock))

    }.catch { t ->
        reportingService.logNonFatalError(t, mapOf(
            "currentMonthPeriod" to currentMonthPeriod,
            "productId" to productId,
        ))
        emit(ResponseWrapper.Failed())
    }
}