package com.hezapp.ekonomis.app_test_utils.product_detail.application.assert_attr

import com.hezapp.ekonomis.base_test_utils.base_assert_attr.AssertionModel
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.product_detail.application.dto.ProductTransaction

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