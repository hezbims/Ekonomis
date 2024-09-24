package com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.profile

import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.model.CreateNewProfileError
import com.hezapp.ekonomis.core.domain.profile.repo.IProfileRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AddNewProfileUseCase(
    private val repo: IProfileRepo
) {
    operator fun invoke(newProfile: ProfileEntity) : Flow<ResponseWrapper<Any?, CreateNewProfileError>> =
    flow<ResponseWrapper<Any?, CreateNewProfileError>> {
        emit(ResponseWrapper.Loading())

        if (newProfile.name.isEmpty()){
            emit(ResponseWrapper.Failed(CreateNewProfileError.NameCantBeEmpty))
            return@flow
        }

        val profileWithSameName = repo.getPersonFiltered(
            profileName = newProfile.name,
            profileType = newProfile.type,
        )
        if (profileWithSameName.isNotEmpty()){
            emit(ResponseWrapper.Failed(CreateNewProfileError.NameAlreadyExist))
            return@flow
        }


        repo.addNewProfile(newProfile)
        emit(ResponseWrapper.Succeed(null))
    }.catch {
        emit(ResponseWrapper.Failed())
    }
}