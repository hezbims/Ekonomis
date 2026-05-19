package com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.impl

import com.hezapp.ekonomis.core.data.product.dao.ProductDao
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.GetProductByIdError
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.ProductByIdPreviewDto
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.iface.IGetProductByIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private typealias Result = ResponseWrapper<ProductByIdPreviewDto, GetProductByIdError>

class GetProductByIdUseCase(
    private val dao: ProductDao,
) : IGetProductByIdUseCase {

    override fun invoke(id: Int) : Flow<Result> = flow {
        emit(ResponseWrapper.Loading())

        val finalResult : Result = dao.getProduct(id)?.let {
            ResponseWrapper.Succeed(data = ProductByIdPreviewDto(name = it.name))
        }  ?: ResponseWrapper.Failed(GetProductByIdError.NotFound)

        emit(finalResult)
    }
}