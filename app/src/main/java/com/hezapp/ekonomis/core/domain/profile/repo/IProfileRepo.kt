package com.hezapp.ekonomis.core.domain.profile.repo

import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.profile.model.CreateNewProfileError
import kotlinx.coroutines.flow.Flow

interface IProfileRepo {

    fun getPersonFiltered(
        profileName: String,
        profileType: ProfileType?,
    ) : Flow<ResponseWrapper<List<ProfileEntity>, MyBasicError>>

    fun addNewProfile(profile : ProfileEntity) : Flow<ResponseWrapper<Any?, CreateNewProfileError>>

}