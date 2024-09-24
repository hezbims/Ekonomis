package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.profile.repo.IProfileRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetListProfileUseCase(
    private val repo : IProfileRepo
) {
    operator fun invoke(
        profileName: String,
        profileType: ProfileType,
    ) : Flow<ResponseWrapper<List<ProfileEntity>, MyBasicError>> =
    flow<ResponseWrapper<List<ProfileEntity>, MyBasicError>> {
        emit(ResponseWrapper.Loading())
        val result = repo.getPersonFiltered(profileName = profileName, profileType = profileType)
        emit(ResponseWrapper.Succeed(result))
    }.catch { emit(ResponseWrapper.Failed()) }
}