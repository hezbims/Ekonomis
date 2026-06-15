package com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto

import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

/**
 * Value object for product quantity.
 *
 * Examples:
 * - [Quantity.piece]() — piece unit
 * - [Quantity.carton]() — carton unit
 *
 * @property unitType the unit type ([com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType.PIECE] or [com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType.CARTON])
 * @property amount the quantity amount
 */
data class QuantityData(
    val unitType: UnitType,
    val amount: Int,
) {
    companion object {
        fun piece(amount: Int = 1) = QuantityData(UnitType.PIECE, amount)
        fun carton(amount: Int = 1) = QuantityData(UnitType.CARTON, amount)
    }
}