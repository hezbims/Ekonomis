package com.hezapp.ekonomis.core.domain.profile.repo

import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType

interface IProfileRepo {

    suspend fun getPersonFiltered(
        profileName: String,
        profileType: ProfileType?,
    ) : List<ProfileEntity>

    suspend fun addNewProfile(profile : ProfileEntity) : Long

}