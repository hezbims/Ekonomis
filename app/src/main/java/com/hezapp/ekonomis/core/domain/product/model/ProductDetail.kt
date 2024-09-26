package com.hezapp.ekonomis.core.domain.product.model

import androidx.room.ColumnInfo
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.utils.PriceUtils
import kotlin.math.roundToInt

data class ProductDetail(
    val id: Int,
    val productName: String,
    val outProductTransactions : List<ProductTransaction>,
    val inProductTransactions : List<ProductTransaction>,
){
    val totalOutPrice = outProductTransactions.sumOf {
        it.price.toLong()
    }
    val totalInPrice = inProductTransactions.sumOf {
        it.price.toLong()
    }
}

data class ProductTransaction(
    val id: Int,

    val price: Int,

    val quantity: Int,

    @ColumnInfo("unit_type")
    val unitType: UnitType,

    val date: Long,

    val ppn: Int?,

    @ColumnInfo("profile_name")
    val profileName : String,
){
    val costOfGoodsSold : Int
        get () = PriceUtils.getCostOfGoodsSoldUseCase(
            ppn = ppn,
            quantity = quantity,
            totalPrice = price,
        ).roundToInt()
}