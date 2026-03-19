package com.hezapp.ekonomis.test_utils.seeder.snapshot

import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType

data class InvoiceSnapshot(
    val id: Int,
    val date: Long,
    val ppn: Int?,
    val transactionType: TransactionType,
    val paymentMedia: PaymentMedia,
    val profile: ProfileSnapshot,
    val invoiceItems: List<InvoiceItemSnapshot>,
    val installment: InstallmentSnapshot?,
)