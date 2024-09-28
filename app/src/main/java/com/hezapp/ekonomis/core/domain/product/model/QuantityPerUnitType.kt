package com.hezapp.ekonomis.core.domain.product.model

import androidx.room.ColumnInfo

data class QuantityPerUnitType(
    @ColumnInfo("carton_quantity")
    val cartonQuantity: Int,

    @ColumnInfo("piece_quantity")
    val pieceQuantity: Int,
)
