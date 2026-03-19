package com.hezapp.ekonomis.test_utils.seeder.snapshot

import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType

data class ProfileSnapshot(
    val id: Int,
    val name: String,
    val type: ProfileType,
)