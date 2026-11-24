package com.hezapp.ekonomis.transaction_history.application.use_case.impl

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.IPerformanceReporter
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.traced
import com.hezapp.ekonomis.transaction_history.application.dto.PreviewTransactionHistory
import com.hezapp.ekonomis.transaction_history.application.use_case.iface.IGetPreviewTransactionHistoryUseCase
import com.hezapp.ekonomis.transaction_history.data.dao.GetPreviewTransactionsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetPreviewTransactionHistoryUseCase(
    private val dao: GetPreviewTransactionsDao,
    private val timeService: ITimeService,
    private val reportingService : IErrorReportingService,
    private val performanceReporter : IPerformanceReporter,
) : IGetPreviewTransactionHistoryUseCase {

    override operator fun invoke(filter: PreviewTransactionFilter) :
            Flow<ResponseWrapper<List<PreviewTransactionHistory>, MyBasicError>> =
    flow<ResponseWrapper<List<PreviewTransactionHistory>, MyBasicError>> {
        emit(ResponseWrapper.Loading())

        val transactionPreviews = dao.execute(
            currentMonthYear = filter.monthYear,
            isOnlyNotPaidOff = filter.isOnlyNotPaidOff,
            timeService = timeService,
        ).map { queryResult ->
            PreviewTransactionHistory.fromQueryResult(queryResult)
        }

        emit(ResponseWrapper.Succeed(transactionPreviews))
    }
    .traced(
        reporter = performanceReporter,
        name = TRACE_NAME,
        attributes = mapOf("only_unpaid" to filter.isOnlyNotPaidOff.toString()),
        metricOf = { response ->
            if (response is ResponseWrapper.Succeed)
                "row_count" to response.data.size.toLong()
            else null
        },
    )
    .catch { t ->
        reportingService.logNonFatalError(t , mapOf(
            "monthYear" to filter.monthYear,
            "isOnlyNotPaidOff" to filter.isOnlyNotPaidOff,
        ))
        emit(ResponseWrapper.Failed())
    }

    companion object {
        private const val TRACE_NAME = "uc_tx_history_preview"
    }
}
