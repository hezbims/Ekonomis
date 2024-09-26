package com.hezapp.ekonomis.core.domain.product.repo

import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary

interface IProductRepo {
    suspend fun getAllProduct(searchQuery : String) : List<ProductEntity>
    suspend fun insertProduct(newProduct: ProductEntity)
    suspend fun getPreviewProductSummaries() : List<PreviewProductSummary>
    suspend fun getProduct(productId: Int) : ProductEntity
}