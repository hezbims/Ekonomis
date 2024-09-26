package com.hezapp.ekonomis.core.data.product.repo

import com.hezapp.ekonomis.core.data.product.dao.ProductDao
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo

class ProductRepo(
    private val dao : ProductDao
) : IProductRepo {
    override suspend fun getAllProduct(searchQuery: String): List<ProductEntity> =
        dao.getAllProducts(searchQuery)

    override suspend fun insertProduct(newProduct: ProductEntity) =
        dao.insertNewProduct(newProduct)

    override suspend fun getPreviewProductSummaries(): List<PreviewProductSummary> {
        return dao.getPreviewProductSummaries()
    }

    override suspend fun getProduct(productId: Int): ProductEntity {
        return dao.getProduct(id = productId)
    }
}