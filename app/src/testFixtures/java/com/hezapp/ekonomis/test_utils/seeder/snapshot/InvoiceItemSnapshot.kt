package com.hezapp.ekonomis.test_utils.seeder.snapshot

import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

data class InvoiceItemSnapshot(
    val id: Int,
    val product: ProductSnapshot,
    val quantity: Int,
    val price: Int,
    val unitType: UnitType,
)