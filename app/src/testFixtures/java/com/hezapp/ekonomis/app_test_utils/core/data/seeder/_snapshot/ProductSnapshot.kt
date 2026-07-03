package com.hezapp.ekonomis.app_test_utils.core.data.seeder._snapshot

import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity

data class ProductSnapshot(
    val id: Int,
    val name: String,
) {
    companion object {
        fun fromRoomEntity(productEntity: ProductEntity) : ProductSnapshot {
            return ProductSnapshot(
                id = productEntity.id,
                name = productEntity.name,
            )
        }
    }
}