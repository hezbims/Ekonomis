package com.hezapp.ekonomis.core.domain.product.model

import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.utils.PriceUtils
import com.hezapp.ekonomis.product_preview.data.dto.PreviewProductSummaryQueryResult
import kotlin.math.roundToInt

data class PreviewProductSummary(
    val id : Int,

    val name : String,

    val quantity: Int?,

    val  price: Int?,

    val ppn : Int?,

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

    companion object {
        fun fromQueryResult(queryResult: PreviewProductSummaryQueryResult) : PreviewProductSummary {
            return PreviewProductSummary(
                id = queryResult.id,
                name = queryResult.name,
                quantity = queryResult.quantity,
                price = queryResult.price,
                ppn = queryResult.ppn,
                unitType = queryResult.unitType,
            )
        }
    }
}