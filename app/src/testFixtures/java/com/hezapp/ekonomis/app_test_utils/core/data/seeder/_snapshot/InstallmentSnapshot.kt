package com.hezapp.ekonomis.app_test_utils.core.data.seeder._snapshot

data class InstallmentSnapshot(
    val id: Int,
    val isPaidOff: Boolean,
    val items: List<InstallmentItemSnapshot>,
)