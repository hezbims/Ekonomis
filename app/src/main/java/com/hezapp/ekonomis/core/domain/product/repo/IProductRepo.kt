package com.hezapp.ekonomis.core.domain.product.repo

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.model.InsertProductError
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import kotlinx.coroutines.flow.Flow

interface IProductRepo {
    fun getAllProduct(searchQuery : String) : Flow<ResponseWrapper<List<ProductEntity>, MyBasicError>>
    fun insertProduct(newProduct: ProductEntity) : Flow<ResponseWrapper<Any?, InsertProductError>>
    suspend fun getPreviewProductSummaries() : List<PreviewProductSummary>
    suspend fun getProductDetail(
        productId: Int,
        monthYearPeriod: Long,
    ) : ProductDetail
}