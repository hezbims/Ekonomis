package com.hezapp.ekonomis.core.data.profile.repo

import com.hezapp.ekonomis.core.data.profile.dao.ProfileDao
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.profile.repo.IProfileRepo

class ProfileRepo(
    private val dao: ProfileDao
) : IProfileRepo {
    override suspend fun getPersonFiltered(
        profileName: String,
        profileType: ProfileType?
    ): List<ProfileEntity> {
        return dao.getListProfile(
            profileName = profileName,
            profileTypeId = profileType?.id,
        )
    }

    override suspend fun addNewProfile(profile: ProfileEntity) : Long {
        return dao.insertProfile(profile)
    }
}