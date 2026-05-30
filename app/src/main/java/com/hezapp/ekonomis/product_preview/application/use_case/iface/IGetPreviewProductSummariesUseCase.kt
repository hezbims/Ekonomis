package com.hezapp.ekonomis.product_preview.application.use_case.iface

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import kotlinx.coroutines.flow.Flow

interface IGetPreviewProductSummariesUseCase {
    operator fun invoke(
        searchQuery: String,
    ): Flow<ResponseWrapper<List<PreviewProductSummary>, MyBasicError>>
}