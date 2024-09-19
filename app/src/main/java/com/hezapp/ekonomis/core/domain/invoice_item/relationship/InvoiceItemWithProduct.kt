package com.hezapp.ekonomis.core.domain.invoice_item.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity

data class InvoiceItemWithProduct(
    @Embedded
    val invoiceItem : InvoiceItemEntity,

    @Relation(
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product : ProductEntity
)