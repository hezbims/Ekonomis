package com.hezapp.ekonomis.assertion.product_detail.application

import com.hezapp.ekonomis.assertion._base.AssertionModel
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction

data class ExpectedProductTransaction(
    val price: Int,
    val quantity: Int,
    val unitType: UnitType,
    val date: Long,
    val ppn: Int?,
    val profileName: String,
) : AssertionModel<ProductTransaction>() {

    override fun matches(actual: ProductTransaction) {
        verifyAll(
            assertEquals(price, actual.price),
            assertEquals(quantity, actual.quantity),
            assertEquals(unitType, actual.unitType),
            assertEquals(date, actual.date),
            assertEquals(ppn, actual.ppn),
            assertEquals(profileName, actual.profileName),
        )
    }
}