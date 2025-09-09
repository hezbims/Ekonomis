package com.hezapp.ekonomis.test_utils.seeder

import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis._testing_only.test_dao.ProductTestDao
import org.koin.core.Koin
import org.koin.core.context.GlobalContext

class ProductSeeder(
    koin: Koin = GlobalContext.get(),
) {
    private val dao: ProductTestDao = koin.get()
    suspend fun run(products: List<ProductEntity>) : List<ProductEntity> {
        val ids = dao.insertNewProducts(products)
        return dao.getByIds(ids.map(Long::toInt))
    }
}