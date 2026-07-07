package com.hezapp.ekonomis.product_detail.application.dto

import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.utils.PriceUtils
import com.hezapp.ekonomis.product_detail.data.dto.ProductTransactionQueryResult
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
    val firstDayOfMonthStock : ProductQuantityDto
        get() = transactionSummary.firstDayOfMonthStock!!
    val monthlyStockId : Int
        get() = transactionSummary.monthlyStockId
    val totalOutUnit : ProductQuantityDto
        get() = transactionSummary.totalOutUnit
    val totalInUnit : ProductQuantityDto
        get() = transactionSummary.totalInUnit
    val latestDayOfMonthStock : ProductQuantityDto
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
    val firstDayOfMonthStock: ProductQuantityDto?,
    val monthlyStockId: Int,
){
    val totalOutUnit : ProductQuantityDto = outProductTransactions.getQuantityPerUnit()

    val totalInUnit : ProductQuantityDto = inProductTransactions.getQuantityPerUnit()

    val latestDayOfMonthStock : ProductQuantityDto =
        (firstDayOfMonthStock ?: ProductQuantityDto(cartonQuantity = 0, pieceQuantity = 0)) -
        totalOutUnit +
        totalInUnit
}

data class ProductTransaction(
    val id: Int,

    val price: Int,

    val quantity: Int,

    val unitType: UnitType,

    val date: Long,

    val ppn: Int?,

    val profileName : String,
){
    val costOfGoodsSold : Int
        get () = PriceUtils.getCostOfGoodsSoldUseCase(
            ppn = ppn,
            quantity = quantity,
            totalPrice = price,
        ).roundToInt()

    companion object {
        fun fromQueryResult(queryResult: ProductTransactionQueryResult) : ProductTransaction {
            return ProductTransaction(
                id = queryResult.id,
                price = queryResult.price,
                quantity = queryResult.quantity,
                unitType = queryResult.unitType,
                date = queryResult.date,
                ppn = queryResult.ppn,
                profileName = queryResult.profileName,
            )
        }
    }
}

operator fun ProductQuantityDto.minus(other: ProductQuantityDto) : ProductQuantityDto {
    return ProductQuantityDto(
        cartonQuantity = cartonQuantity - other.cartonQuantity,
        pieceQuantity = pieceQuantity - other.pieceQuantity,
    )
}

operator fun ProductQuantityDto.plus(other: ProductQuantityDto) : ProductQuantityDto {
    return ProductQuantityDto(
        cartonQuantity = cartonQuantity + other.cartonQuantity,
        pieceQuantity = pieceQuantity + other.pieceQuantity,
    )
}

fun List<ProductTransaction>.getQuantityPerUnit() : ProductQuantityDto {
    return fold(ProductQuantityDto(0, 0)){ prevQuantity, productTransaction ->
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