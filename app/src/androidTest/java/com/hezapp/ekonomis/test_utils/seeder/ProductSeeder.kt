package com.hezapp.ekonomis.test_utils.seeder

import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.test_utils.test_dao.ProductTestDao
import org.koin.core.context.GlobalContext

class ProductSeeder(
    private val dao: ProductTestDao = GlobalContext.get().get(),
) {
    suspend fun run(products: List<ProductEntity>) : List<ProductEntity> {
        val ids = dao.insertNewProducts(products)
        return dao.getByIds(ids.map(Long::toInt))
    }
}