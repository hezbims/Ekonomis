package com.hezapp.ekonomis.core.domain.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.hezapp.ekonomis.core.domain.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.entity.ProductEntity

data class InvoiceItemWithProduct(
    @Embedded
    val invoiceItem : InvoiceItemEntity,

    @Relation(
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product : ProductEntity
)