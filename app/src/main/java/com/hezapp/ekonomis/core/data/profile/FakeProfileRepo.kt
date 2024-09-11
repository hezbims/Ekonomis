package com.hezapp.ekonomis.core.data.profile

import com.hezapp.ekonomis.core.domain.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.profile.CreateNewProfileError
import com.hezapp.ekonomis.core.domain.profile.IProfileRepo
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
        listPerson.add(profile.copy(id = listPerson.size + 1))
        emit(ResponseWrapper.Succeed(null))
    }

    private companion object {
        val listPerson = mutableListOf(
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
        )
    }
}