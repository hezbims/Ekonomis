package com.hezapp.ekonomis.core.domain.monthly_stock.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity

@Entity(
    foreignKeys = [
        ForeignKey(
            childColumns = ["product_id"],
            parentColumns = ["id"],
            entity = ProductEntity::class
        )
    ],
    tableName = "monthly_stock",
    indices = [
        Index("product_id", "month_year_period", unique = true)
    ]
)
data class MonthlyStockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "month_year_period")
    val monthYearPeriod: Long,

    @Embedded
    val quantityPerUnitType: QuantityPerUnitType,

    @ColumnInfo(name = "product_id")
    val productId: Int
)

data class QuantityPerUnitType(
    @ColumnInfo(name = "carton_quantity")
    val cartonQuantity: Int,

    @ColumnInfo(name = "piece_quantity")
    val pieceQuantity: Int,
)