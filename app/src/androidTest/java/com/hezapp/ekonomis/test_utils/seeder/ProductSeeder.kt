package com.hezapp.ekonomis.test_utils.seeder

import com.hezapp.ekonomis.core.data.product.dao.ProductDao
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import org.koin.core.context.GlobalContext

class ProductSeeder(
    private val dao: ProductDao = GlobalContext.get().get(),
) {
    suspend fun run(products: List<ProductEntity>) : List<ProductEntity> {
        val ids = dao.insertNewProducts(products)
        return dao.getProductsByIds(ids.map(Long::toInt))
    }
}