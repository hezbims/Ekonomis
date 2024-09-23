package com.hezapp.ekonomis.core.data.profile

import com.hezapp.ekonomis.BuildConfig
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.profile.model.CreateNewProfileError
import com.hezapp.ekonomis.core.domain.profile.repo.IProfileRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProfileRepo : IProfileRepo {
    override fun getPersonFiltered(
        profileName: String,
        profileType: ProfileType?
    ): Flow<ResponseWrapper<List<ProfileEntity>, MyBasicError>> {
        return flow {
            emit(ResponseWrapper.Loading())
            delay(100L)
            val filteredPerson = listPerson.filter {
                it.name.contains(profileName, ignoreCase = true) && (
                    if (profileType == null) true else it.type == profileType
                )
            }
            emit(ResponseWrapper.Succeed(filteredPerson))
        }
    }

    override fun addNewProfile(profile: ProfileEntity): Flow<ResponseWrapper<Any?, CreateNewProfileError>> = flow {
        emit(ResponseWrapper.Loading())
        delay(100L)
        if (profile.name.isEmpty()){
            emit(ResponseWrapper.Failed(CreateNewProfileError.NameCantBeEmpty))
            return@flow
        }
        else if (listPerson.firstOrNull {
            it.name.equals(profile.name, ignoreCase = true) && it.type == profile.type
        } != null){
            emit(ResponseWrapper.Failed(CreateNewProfileError.NameAlreadyExist))
            return@flow
        }
        listPerson.add(profile.copy(id = id++))
        emit(ResponseWrapper.Succeed(null))
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