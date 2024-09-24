package com.hezapp.ekonomis.core.data.profile.repo

import com.hezapp.ekonomis.BuildConfig
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.profile.repo.IProfileRepo
import kotlinx.coroutines.delay

class FakeProfileRepo : IProfileRepo {
    override suspend fun getPersonFiltered(
        profileName: String,
        profileType: ProfileType?
    ): List<ProfileEntity> {
        delay(100L)
        val filteredPerson = listPerson.filter {
            it.name.contains(profileName, ignoreCase = true) && (
                if (profileType == null) true else it.type == profileType
            )
        }
        return filteredPerson
    }

    override suspend fun addNewProfile(profile: ProfileEntity) : Long  {
        listPerson.add(profile.copy(id = id++))
        return id.toLong() - 1
    }

    companion object {
        val listPerson = if (BuildConfig.DEBUG) mutableListOf(
            ProfileEntity(
                id = 1,
                name = "Beni",
                type = ProfileType.SUPPLIER
            ),
            ProfileEntity(
                id = 2,
                name = "Feni",
                type = ProfileType.CUSTOMER
            ),
            ProfileEntity(
                id = 3,
                name = "Komang",
                type = ProfileType.SUPPLIER
            ),
        ) else mutableListOf()
        var id = listPerson.size + 1
    }
}