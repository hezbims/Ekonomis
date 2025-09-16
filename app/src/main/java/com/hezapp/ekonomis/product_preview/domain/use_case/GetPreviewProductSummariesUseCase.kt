package com.hezapp.ekonomis.product_preview.domain.use_case

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetPreviewProductSummariesUseCase(
    private val repo : IProductRepo,
    private val reportingService: IErrorReportingService,
) {
    operator fun invoke(
        searchQuery: String,
    ) : Flow<ResponseWrapper<List<PreviewProductSummary>, MyBasicError>> =
    flow<ResponseWrapper<List<PreviewProductSummary>, MyBasicError>> {
        emit(ResponseWrapper.Loading())
        emit(ResponseWrapper.Succeed(repo.getPreviewProductSummaries(
            searchQuery = searchQuery,
        )))
    }.catch { t ->
        reportingService.logNonFatalError(t , mapOf("searchQuery" to searchQuery))
        emit(ResponseWrapper.Failed())
    }
}