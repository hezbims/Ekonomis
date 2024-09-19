package com.hezapp.ekonomis.core.domain.product.model

import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

data class PreviewProductSummary(
    val id : Int,
    val name : String,
    // harga pokok
    val costOfGoodsSold : Int?,
    val unitType: UnitType?,
)