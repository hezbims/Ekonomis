package com.hezapp.ekonomis.core.domain.product

import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType
import com.hezapp.ekonomis.core.domain.utils.PriceUtils
import kotlin.math.roundToInt

data class ProductDetail(
    val id: Int,
    val name: String,
    val outProductTransactions : List<ProductTransaction>,
    val inProductTransactions : List<ProductTransaction>,
)

data class ProductTransaction(
    val id: Int,
    val totalPrice: Int,
    val quantity: Int,
    val unitType: UnitType,
    val date: Long,
    val ppn: Int?,
){
    val costOfGoodsSold : Int
        get () = PriceUtils.getCostOfGoodsSoldUseCase(
            ppn = ppn,
            quantity = quantity,
            totalPrice = totalPrice,
        ).roundToInt()
}