package com.hezapp.ekonomis.core.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("product_id"),
            onUpdate = ForeignKey.RESTRICT,
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("invoice_id"),
            onUpdate = ForeignKey.RESTRICT,
            onDelete = ForeignKey.RESTRICT
        ),
    ]
)
data class InvoiceItemEntity(
    @PrimaryKey
    val id : Int = 0,

    @ColumnInfo(name = "product_id")
    val productId : Int,

    @ColumnInfo(name = "invoice_id")
    val invoiceId : Int,

    val quantity : Int,

    val price : Int,

    @ColumnInfo(name = "unit_type")
    val unitType : UnitType,
)
