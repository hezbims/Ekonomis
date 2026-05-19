package com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.iface

import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.GetProductByIdError
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.ProductByIdPreviewDto
import kotlinx.coroutines.flow.Flow

interface IGetProductByIdUseCase {
    operator fun invoke(id: Int) : Flow<ResponseWrapper<ProductByIdPreviewDto, GetProductByIdError>>
}