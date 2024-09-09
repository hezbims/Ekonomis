package com.hezapp.ekonomis.add_new_transaction.presentation.model

import com.hezapp.ekonomis.core.domain.entity.relationship.InvoiceItemWithProduct
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType

data class InvoiceItemUiModel(
    val id: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Int,
    val unitType: UnitType,
)

fun InvoiceItemWithProduct.toUiModel() : InvoiceItemUiModel =
    InvoiceItemUiModel(
        id = invoiceItem.invoiceId,
        productId = product.id,
        productName = product.name,
        quantity = invoiceItem.quantity,
        price = invoiceItem.price,
        unitType = invoiceItem.unitType
    )