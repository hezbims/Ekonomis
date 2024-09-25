package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product

import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.model.InsertProductError
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class InsertNewProductUseCase(
    private val repo: IProductRepo
) {
    operator fun invoke(newProduct: ProductEntity): Flow<ResponseWrapper<Any?, InsertProductError>> =
    flow<ResponseWrapper<Any?, InsertProductError>> {
        emit(ResponseWrapper.Loading())


        if (newProduct.name.isEmpty()) {
            emit(ResponseWrapper.Failed(InsertProductError.EmptyInputName))
            return@flow
        }


        val productWithSameName = repo.getAllProduct(newProduct.name)
        if (productWithSameName.isNotEmpty()) {
            emit(ResponseWrapper.Failed(InsertProductError.AlreadyUsed))
            return@flow
        }


        repo.insertProduct(newProduct)
        emit(ResponseWrapper.Succeed(null))
    }.catch { emit(ResponseWrapper.Failed()) }
}