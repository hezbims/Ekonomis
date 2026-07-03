package com.hezapp.ekonomis.app_test_utils.core.data.seeder.profile

import com.hezapp.ekonomis.app_test_utils.core.data.seeder._snapshot.ProfileSnapshot
import com.hezapp.ekonomis.core.data.profile.dao.ProfileDao
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import org.koin.core.Koin
import org.koin.core.context.GlobalContext

class ProfileSeeder(
    koin: Koin = GlobalContext.get(),
) {
    private val profileDao: ProfileDao = koin.get()

    suspend fun runV2(profileName: String, profileType: ProfileType) : ProfileSnapshot {
        val ids = profileDao.insertProfiles(listOf(
            ProfileEntity(name = profileName, type = profileType)
        ))
        return profileDao.getProfilesByIds(ids.map(Long::toInt))
            .map(ProfileSnapshot.Companion::fromRoomEntity)
            .single()
    }

    @Deprecated(
        message = "Replace with runV2 so it don't immediately return ProfileEntity",
        replaceWith = ReplaceWith("runV2(profileName, profileType)")
    )
    suspend fun run(profileName: String, profileType: ProfileType) : ProfileEntity {
        val ids = profileDao.insertProfiles(listOf(
            ProfileEntity(name = profileName, type = profileType)
        ))
        return profileDao.getProfilesByIds(ids.map(Long::toInt)).single()
    }
}