package com.hezapp.ekonomis.core.domain.product.model

import androidx.room.ColumnInfo
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.utils.PriceUtils
import kotlin.math.roundToInt

data class PreviewProductSummary(
    val id : Int,

    val name : String,

    val quantity: Int?,

    val  price: Int?,

    val ppn : Int?,

    @ColumnInfo(name = "unit_type")
    val unitType: UnitType?,
){
    // harga pokok
    val costOfGoodsSold : Int?
        get(){
            if (quantity != null && price != null && quantity > 0)
                return PriceUtils.getCostOfGoodsSoldUseCase(
                    totalPrice = price,
                    ppn = ppn,
                    quantity = quantity
                ).roundToInt()

            return null
        }
}