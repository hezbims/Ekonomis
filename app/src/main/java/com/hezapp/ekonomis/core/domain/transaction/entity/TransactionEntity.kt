package com.hezapp.ekonomis.core.domain.transaction.entity

import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType

data class TransactionEntity(
    val id: Int,
    val transactionType: TransactionType,
    val profileId: Int,
    val transactionDateMillis: Long,
    val ppn: Int?,
    val items: List<TransactionItemEntity>,
)

data class TransactionItemEntity(
    val productId : Int,
    val quantity : Int,
    val price : Int,
    val unitType : UnitType,
)