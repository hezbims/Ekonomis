package com.hezapp.ekonomis.test_utils.seeder.snapshot

data class InstallmentSnapshot(
    val id: Int,
    val isPaidOff: Boolean,
    val items: List<InstallmentItemSnapshot>,
)