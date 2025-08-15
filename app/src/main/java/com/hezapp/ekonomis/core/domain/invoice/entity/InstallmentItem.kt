package com.hezapp.ekonomis.core.domain.invoice.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "installment_items",
    foreignKeys = [
        ForeignKey(
            entity = Installment::class,
            parentColumns = ["id"],
            childColumns = ["installment_id"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index("installment_id"),
    ]
)
class InstallmentItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "installment_id")
    val installmentId : Int,
    @ColumnInfo(name = "payment_date")
    val paymentDate: LocalDate,
    val amount: Int,
)