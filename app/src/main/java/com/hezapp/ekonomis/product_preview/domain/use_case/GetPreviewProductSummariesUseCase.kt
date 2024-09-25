package com.hezapp.ekonomis.product_preview.domain.use_case

import com.hezapp.ekonomis.core.data.product.repo.FakeProductRepo
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetPreviewProductSummariesUseCase {
    private val repo : IProductRepo = FakeProductRepo()
    operator fun invoke() : Flow<ResponseWrapper<List<PreviewProductSummary>, MyBasicError>> =
    flow<ResponseWrapper<List<PreviewProductSummary>, MyBasicError>> {
        emit(ResponseWrapper.Loading())
        emit(ResponseWrapper.Succeed(repo.getPreviewProductSummaries()))
    }.catch { emit(ResponseWrapper.Failed()) }
}