package com.hezapp.ekonomis.product_preview.data.dto

import androidx.room.ColumnInfo
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

data class PreviewProductSummaryQueryResult(
    val id : Int,

    val name : String,

    val quantity: Int?,

    val  price: Int?,

    val ppn : Int?,

    @ColumnInfo(name = "unit_type")
    val unitType: UnitType?,
)