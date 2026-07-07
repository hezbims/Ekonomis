package com.hezapp.ekonomis.product_detail.application.dto

import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType

data class ProductQuantityDto(
    val cartonQuantity: Int,
    val pieceQuantity: Int,
){
    companion object {
        fun fromQueryResult(queryResult: QuantityPerUnitType?) : ProductQuantityDto {
            return ProductQuantityDto(
                cartonQuantity = queryResult?.cartonQuantity ?: 0,
                pieceQuantity = queryResult?.pieceQuantity ?: 0,
            )
        }
    }

    fun toQueryResult() : QuantityPerUnitType {
        return QuantityPerUnitType(
            cartonQuantity = cartonQuantity,
            pieceQuantity = pieceQuantity,
        )
    }
}
