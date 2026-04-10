package com.hezapp.ekonomis.core.domain.invoice.entity

import androidx.room.*
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
        Index("profile_id"),
    //TODO : tambahkan index untuk date
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


    /**
     * if invoice entity doesn't have any `InstallmentItem`, than this `paymentMedia`
     * will not be ignored in business logic. And it also means this Invoice is paid
     * immediately
     */
    @ColumnInfo(name = "payment_media", defaultValue = "0")
    val paymentMedia: PaymentMedia = PaymentMedia.TRANSFER
)