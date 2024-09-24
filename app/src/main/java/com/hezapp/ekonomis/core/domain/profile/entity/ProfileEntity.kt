package com.hezapp.ekonomis.core.domain.profile.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val name : String,
    val type : ProfileType
)