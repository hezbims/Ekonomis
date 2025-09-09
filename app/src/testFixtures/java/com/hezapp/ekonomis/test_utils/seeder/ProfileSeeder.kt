package com.hezapp.ekonomis.test_utils.seeder

import com.hezapp.ekonomis.core.data.profile.dao.ProfileDao
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import org.koin.core.Koin
import org.koin.core.context.GlobalContext

class ProfileSeeder(
    koin: Koin = GlobalContext.get(),
) {
    private val profileDao: ProfileDao = koin.get()
    suspend fun run(profileName: String, profileType: ProfileType) : ProfileEntity {
        val ids = profileDao.insertProfiles(listOf(
            ProfileEntity(name = profileName, type = profileType)
        ))
        return profileDao.getProfilesByIds(ids.map(Long::toInt)).single()
    }
}