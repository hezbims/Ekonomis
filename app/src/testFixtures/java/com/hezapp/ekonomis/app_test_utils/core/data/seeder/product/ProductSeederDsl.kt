package com.hezapp.ekonomis.app_test_utils.core.data.seeder.product

import com.hezapp.ekonomis._testing_only.test_dao.ProductTestDao
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.app_test_utils.core.data.seeder.SeederDsl
import com.hezapp.ekonomis.app_test_utils.core.data.seeder._snapshot.ProductSnapshot
import kotlinx.coroutines.runBlocking

fun SeederDsl.product(name: String) : ProductSnapshot = runBlocking {
    val dao = koin.get<ProductTestDao>()

    val ids = dao.insertNewProducts(listOf(ProductEntity(name = name)))

    dao.getByIds(ids.map(Long::toInt))
        .map(ProductSnapshot::fromRoomEntity)
        .single()
}