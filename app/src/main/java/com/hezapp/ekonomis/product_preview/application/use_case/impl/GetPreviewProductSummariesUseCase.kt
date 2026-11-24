package com.hezapp.ekonomis.product_preview.application.use_case.impl

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.IPerformanceReporter
import com.hezapp.ekonomis.core.domain.utils.traced
import com.hezapp.ekonomis.product_preview.application.use_case.iface.IGetPreviewProductSummariesUseCase
import com.hezapp.ekonomis.product_preview.data.dao.GetPreviewProductSummariesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetPreviewProductSummariesUseCase(
    private val dao: GetPreviewProductSummariesDao,
    private val reportingService: IErrorReportingService,
    private val performanceReporter: IPerformanceReporter,
) : IGetPreviewProductSummariesUseCase {
    override operator fun invoke(
        searchQuery: String,
    ) : Flow<ResponseWrapper<List<PreviewProductSummary>, MyBasicError>> =
    flow<ResponseWrapper<List<PreviewProductSummary>, MyBasicError>> {
        emit(ResponseWrapper.Loading())

        val previewProducts = dao.execute(searchQuery = searchQuery).map { queryResult ->
            PreviewProductSummary.fromQueryResult(queryResult)
        }

        emit(ResponseWrapper.Succeed(previewProducts))
    }
    .traced(
        reporter = performanceReporter,
        name = TRACE_NAME,
        metricOf = { response ->
            if (response is ResponseWrapper.Succeed)
                "row_count" to response.data.size.toLong()
            else null
        },
    )
    .catch { t ->
        reportingService.logNonFatalError(t , mapOf("searchQuery" to searchQuery))
        emit(ResponseWrapper.Failed())
    }

    companion object {
        private const val TRACE_NAME = "uc_product_preview"
    }
}
