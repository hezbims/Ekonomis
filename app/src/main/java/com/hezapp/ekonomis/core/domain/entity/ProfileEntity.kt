package com.hezapp.ekonomis.core.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id : Int = 0,
    val name : String,
    val type : ProfileType
)