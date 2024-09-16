package com.hezapp.ekonomis.core.domain.utils

class PriceUtils {
    companion object {
        fun getCostOfGoodsSoldUseCase(
            totalPrice: Int,
            quantity: Int,
            ppn: Int?,
        ) : Double =
            totalPrice.toDouble() / 100 / quantity * ((ppn ?: 0) + 100)
    }
}