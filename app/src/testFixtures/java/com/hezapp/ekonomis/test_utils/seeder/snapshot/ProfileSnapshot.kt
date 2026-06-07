package com.hezapp.ekonomis.test_utils.seeder.snapshot

import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType

data class ProfileSnapshot(
    val id: Int,
    val name: String,
    val type: ProfileType,
){
    companion object {
        fun fromRoomEntity(profileEntity: ProfileEntity) : ProfileSnapshot {
            return ProfileSnapshot(
                id = profileEntity.id,
                name = profileEntity.name,
                type = profileEntity.type,
            )
        }
    }
}