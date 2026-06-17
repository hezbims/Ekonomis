package com.hezapp.ekonomis.test_utils.seeder.dsl.profile

import com.hezapp.ekonomis.core.data.profile.dao.ProfileDao
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.test_utils.seeder.dsl.SeederDsl
import com.hezapp.ekonomis.test_utils.seeder.snapshot.ProfileSnapshot
import kotlinx.coroutines.runBlocking
import org.koin.core.Koin

fun SeederDsl.supplierProfile(name: String) : ProfileSnapshot = thereIsProfile(
    name = name,
    type = ProfileType.SUPPLIER,
    koin = koin,
)

fun SeederDsl.customerProfile(name: String) : ProfileSnapshot = thereIsProfile(
    name = name,
    type = ProfileType.CUSTOMER,
    koin = koin,
)

private fun thereIsProfile(
    name: String,
    type: ProfileType,
    koin: Koin,
) : ProfileSnapshot = runBlocking {
    val profileDao = koin.get<ProfileDao>()

    val ids = profileDao.insertProfiles(listOf(
        ProfileEntity(name = name, type = type)
    ))

    profileDao.getProfilesByIds(ids.map(Long::toInt))
        .map(ProfileSnapshot::fromRoomEntity)
        .single()
}