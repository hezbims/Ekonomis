package com.hezapp.ekonomis.core.transaction.domain.entity

import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import java.time.LocalDate

data class TransactionEntity(
    val id: Int,
    val transactionType: TransactionType,
    val profileId: Int,
    val transactionDateMillis: Long,
    val ppn: Int?,
    val items: List<TransactionItemEntity>,
    val installment: InstallmentEntity?,
    val paymentMedia: PaymentMedia,
)

data class TransactionItemEntity(
    val productId : Int,
    val quantity : Int,
    val price : Int,
    val unitType : UnitType,
)

data class InstallmentEntity(
    val isPaidOff: Boolean,
    val items: List<InstallmentItemEntity>,
)

data class InstallmentItemEntity(
    val paymentDate: LocalDate,
    val amount: Int,
    val paymentMedia: PaymentMedia,
)