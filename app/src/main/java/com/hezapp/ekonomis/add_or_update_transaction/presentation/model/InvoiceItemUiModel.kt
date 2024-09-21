package com.hezapp.ekonomis.add_or_update_transaction.presentation.model

import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.invoice_item.relationship.InvoiceItemWithProduct
import java.util.UUID

data class InvoiceItemUiModel(
    val id: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Int,
    val unitType: UnitType,
    val listId : String,
){
    class Factory(private val initialListId : String?){
        fun create(
            id: Int,
            productId: Int,
            productName: String,
            quantity: Int,
            price: Int,
            unitType: UnitType,
        ) : InvoiceItemUiModel =
        InvoiceItemUiModel(
            id = id,
            productId = productId,
            productName = productName,
            quantity = quantity,
            price = price,
            unitType = unitType,
            listId = initialListId ?: UUID.randomUUID().toString()
        )
    }

    companion object {
        fun new(
            id: Int,
            productId: Int,
            productName: String,
            quantity: Int,
            price: Int,
            unitType: UnitType,
        ) : InvoiceItemUiModel =
            InvoiceItemUiModel(
                id = id,
                productId = productId,
                productName = productName,
                quantity = quantity,
                price = price,
                unitType = unitType,
                listId = UUID.randomUUID().toString()
            )
    }

    fun toInvoiceItemEntity(invoiceId : Int) : InvoiceItemEntity =
        InvoiceItemEntity(
            id = id,
            invoiceId = invoiceId,
            price = price,
            productId = productId,
            quantity = quantity,
            unitType = unitType,
        )
}

fun InvoiceItemWithProduct.toUiModel() : InvoiceItemUiModel =
    InvoiceItemUiModel.new(
        id = invoiceItem.id,
        productId = product.id,
        productName = product.name,
        quantity = invoiceItem.quantity,
        price = invoiceItem.price,
        unitType = invoiceItem.unitType,
    )