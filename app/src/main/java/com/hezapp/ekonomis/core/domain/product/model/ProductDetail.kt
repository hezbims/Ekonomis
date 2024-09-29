package com.hezapp.ekonomis.core.domain.product.model

import androidx.room.ColumnInfo
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.utils.PriceUtils
import kotlin.math.roundToInt

data class ProductDetail(
    val id: Int,
    val productName: String,
    val transactionSummary: TransactionSummary,
) {
    val outProductTransactions : List<ProductTransaction>
        get() = transactionSummary.outProductTransactions
    val inProductTransactions : List<ProductTransaction>
        get() = transactionSummary.inProductTransactions
    val firstDayOfMonthStock : QuantityPerUnitType
        get() = transactionSummary.firstDayOfMonthStock!!
    val monthlyStockId : Int
        get() = transactionSummary.monthlyStockId
    val totalOutUnit : QuantityPerUnitType
        get() = transactionSummary.totalOutUnit
    val totalInUnit : QuantityPerUnitType
        get() = transactionSummary.totalInUnit
    val latestDayOfMonthStock : QuantityPerUnitType
        get() = transactionSummary.latestDayOfMonthStock

    val totalOutPrice = outProductTransactions.sumOf {
        it.price.toLong()
    }
    val totalInPrice = inProductTransactions.sumOf {
        it.price.toLong()
    }
}

data class TransactionSummary(
    val outProductTransactions : List<ProductTransaction>,
    val inProductTransactions : List<ProductTransaction>,
    val firstDayOfMonthStock: QuantityPerUnitType?,
    val monthlyStockId: Int,
){
    val totalOutUnit : QuantityPerUnitType = outProductTransactions.getQuantityPerUnit()

    val totalInUnit : QuantityPerUnitType = inProductTransactions.getQuantityPerUnit()

    val latestDayOfMonthStock : QuantityPerUnitType =
        (firstDayOfMonthStock ?: QuantityPerUnitType(cartonQuantity = 0, pieceQuantity = 0)) -
        totalOutUnit +
        totalInUnit
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

operator fun QuantityPerUnitType.minus(other: QuantityPerUnitType) : QuantityPerUnitType {
    return QuantityPerUnitType(
        cartonQuantity = cartonQuantity - other.cartonQuantity,
        pieceQuantity = pieceQuantity - other.pieceQuantity,
    )
}

operator fun QuantityPerUnitType.plus(other: QuantityPerUnitType) : QuantityPerUnitType {
    return QuantityPerUnitType(
        cartonQuantity = cartonQuantity + other.cartonQuantity,
        pieceQuantity = pieceQuantity + other.pieceQuantity,
    )
}

fun List<ProductTransaction>.getQuantityPerUnit() : QuantityPerUnitType {
    return fold(QuantityPerUnitType(0, 0)){ prevQuantity, productTransaction ->
        when(productTransaction.unitType){
            UnitType.PIECE ->
                prevQuantity.copy(
                    pieceQuantity = productTransaction.quantity + prevQuantity.pieceQuantity
                )
            UnitType.CARTON ->
                prevQuantity.copy(
                    cartonQuantity = productTransaction.quantity + prevQuantity.cartonQuantity
                )
        }
    }
}