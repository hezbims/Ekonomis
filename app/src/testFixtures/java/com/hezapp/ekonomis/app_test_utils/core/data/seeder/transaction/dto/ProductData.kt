package com.hezapp.ekonomis.app_test_utils.core.data.seeder.transaction.dto

/**
 * Unresolved product reference collected during the DSL builder phase.
 * [id] is nullable because it may be auto-resolved at seed time.
 */
internal data class ProductData(
    val id: Int?,
    val quantity: QuantityData,
    val price: Int,
)