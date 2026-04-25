package com.hezapp.ekonomis.edit_product_dialog.application.use_case

import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.edit_product_dialog.application.model.EditProductNameError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class EditProductNameUseCase(
    private val repo: IProductRepo,
    private val reportingService: IErrorReportingService,
) {
    operator fun invoke(
        productId: Int,
        name: String,
    ) : Flow<ResponseWrapper<Any?, EditProductNameError>> =
    flow<ResponseWrapper<Any?, EditProductNameError>> {
        emit(ResponseWrapper.Loading())

        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            emit(ResponseWrapper.Failed(EditProductNameError.EmptyName))
            return@flow
        }

        val existingProduct = repo.getProductByName(trimmedName)
        if (existingProduct != null && existingProduct.id != productId) {
            emit(ResponseWrapper.Failed(EditProductNameError.ProductNameAlreadyExist))
            return@flow
        }

        val currentProduct = repo.getProduct(productId)
        if (currentProduct == null) {
            emit(ResponseWrapper.Failed(EditProductNameError.ProductIdNotFound))
            return@flow
        }

        repo.updateProductName(id = productId, newName = trimmedName)
        emit(ResponseWrapper.Succeed(null))
    }.catch { t ->
        reportingService.logNonFatalError(t, mapOf(
            "productId" to productId,
            "name" to name,
        ))
        emit(ResponseWrapper.Failed())
    }
}