package com.hezapp.ekonomis.core.domain.invoice.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("profile_id"),
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index("profile_id")
    ]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,

    val date : Long,

    @ColumnInfo(name = "profile_id")
    val profileId : Int,

    val ppn : Int?,

    @ColumnInfo(name = "transaction_type")
    val transactionType: TransactionType,
)