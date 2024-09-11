package com.hezapp.ekonomis.core.domain.profile

import com.hezapp.ekonomis.core.domain.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import kotlinx.coroutines.flow.Flow

interface IProfileRepo {

    fun getPersonFiltered(
        profileName: String,
        profileType: ProfileType?,
    ) : Flow<ResponseWrapper<List<ProfileEntity>, MyBasicError>>

    fun addNewProfile(profile : ProfileEntity) : Flow<ResponseWrapper<Any?, CreateNewProfileError>>

}