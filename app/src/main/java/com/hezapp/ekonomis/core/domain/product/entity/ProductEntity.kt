package com.hezapp.ekonomis.core.domain.product.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id : Int,
    val name : String,
)
