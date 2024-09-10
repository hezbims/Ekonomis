package com.hezapp.ekonomis.add_new_transaction.presentation.model

import com.hezapp.ekonomis.core.domain.entity.relationship.InvoiceItemWithProduct
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType
import java.util.UUID

class InvoiceItemUiModel(
    val id: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Int,
    val unitType: UnitType,
    listId : String?,
){
    // UI Operation purpose only
    val listId: String = listId ?: UUID.randomUUID().toString()
}

fun InvoiceItemWithProduct.toUiModel() : InvoiceItemUiModel =
    InvoiceItemUiModel(
        id = invoiceItem.invoiceId,
        productId = product.id,
        productName = product.name,
        quantity = invoiceItem.quantity,
        price = invoiceItem.price,
        unitType = invoiceItem.unitType,
        listId = null,
    )