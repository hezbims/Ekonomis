package com.hezapp.ekonomis.add_new_transaction.domain.person

import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType

data class PersonEntity(
    val id: Int = 0,
    val name: String,
    val type: ProfileType,
)
