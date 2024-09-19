package com.hezapp.ekonomis.core.domain.invoice.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.hezapp.ekonomis.core.domain.invoice_item.relationship.InvoiceItemWithProduct
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity

data class InvoiceWithInvoiceItemAndProducts(
    @Embedded
    val invoice : InvoiceEntity,

    @Relation(
        entity = InvoiceItemEntity::class,
        entityColumn = "invoice_id",
        parentColumn = "id"
    )
    val invoiceItemWithProducts: List<InvoiceItemWithProduct>
)
