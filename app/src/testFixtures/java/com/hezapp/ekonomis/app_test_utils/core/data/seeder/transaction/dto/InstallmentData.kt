package com.hezapp.ekonomis.app_test_utils.core.data.seeder.transaction.dto

internal data class InstallmentData(
    val isPaidOff: Boolean?,
    val paymentSeeds: List<PaymentData>,
)