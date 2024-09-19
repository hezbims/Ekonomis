package com.hezapp.ekonomis.core.domain.profile.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id : Int = 0,
    val name : String,
    val type : ProfileType
)