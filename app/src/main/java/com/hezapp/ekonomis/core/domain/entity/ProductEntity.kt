package com.hezapp.ekonomis.core.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id : Int = 0,
    val name : String,
)
