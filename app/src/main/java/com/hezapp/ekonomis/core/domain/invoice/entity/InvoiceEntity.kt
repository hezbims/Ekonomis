package com.hezapp.ekonomis.core.domain.invoice.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = arrayOf("profile_id"),
            childColumns = arrayOf("id"),
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ]
)
data class InvoiceEntity(
    val id : Int = 0,

    val date : Long,

    @ColumnInfo(name = "profile_id")
    val profileId : Int,

    val ppn : Int?,

    @ColumnInfo(name = "transaction_type")
    val transactionType: TransactionType,
)