package com.hezapp.ekonomis.product_detail.data.dto

import androidx.room.ColumnInfo
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

data class ProductTransactionQueryResult(
    val id: Int,

    val price: Int,

    val quantity: Int,

    @ColumnInfo("unit_type")
    val unitType: UnitType,

    val date: Long,

    val ppn: Int?,

    @ColumnInfo("profile_name")
    val profileName : String,
)
