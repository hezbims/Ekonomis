package com.hezapp.ekonomis.core.domain.product

import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType

data class PreviewProductSummary(
    val id : Int,
    val name : String,
    // harga pokok
    val costOfGoodsSold : Int?,
    val unitType: UnitType?,
)