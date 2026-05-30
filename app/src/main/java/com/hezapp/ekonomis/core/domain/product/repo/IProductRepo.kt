package com.hezapp.ekonomis.core.domain.product.repo

import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity

interface IProductRepo {
    suspend fun getAllProduct(searchQuery : String) : List<ProductEntity>
    suspend fun insertProduct(newProduct: ProductEntity)
    suspend fun getProduct(productId: Int) : ProductEntity?
    suspend fun getProductByName(name: String) : ProductEntity?
    suspend fun updateProductName(id: Int, newName: String)
}