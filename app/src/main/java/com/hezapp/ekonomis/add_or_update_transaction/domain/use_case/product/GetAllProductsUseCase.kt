package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetAllProductsUseCase(
    private val repo : IProductRepo
) {
    operator fun invoke(searchQuery: String) : Flow<ResponseWrapper<List<ProductEntity>, MyBasicError>> =
    flow<ResponseWrapper<List<ProductEntity>, MyBasicError>> {
        emit(ResponseWrapper.Loading())
        emit(ResponseWrapper.Succeed(repo.getAllProduct(searchQuery)))
    }.catch {
        emit(ResponseWrapper.Failed())
    }
}