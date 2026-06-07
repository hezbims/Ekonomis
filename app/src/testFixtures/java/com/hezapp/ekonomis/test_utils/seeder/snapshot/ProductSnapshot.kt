package com.hezapp.ekonomis.test_utils.seeder.snapshot

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