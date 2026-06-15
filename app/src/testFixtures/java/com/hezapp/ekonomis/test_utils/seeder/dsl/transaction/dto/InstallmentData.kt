package com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.dto

internal data class InstallmentData(
    val isPaidOff: Boolean?,
    val paymentSeeds: List<PaymentData>,
)