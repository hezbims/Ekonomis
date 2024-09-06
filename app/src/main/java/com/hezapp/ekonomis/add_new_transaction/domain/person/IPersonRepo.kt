package com.hezapp.ekonomis.add_new_transaction.domain.person

import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import kotlinx.coroutines.flow.Flow

interface IPersonRepo {

    fun getPersonFiltered(
        personName: String = "",
        personType: ProfileType? = null,
    ) : Flow<ResponseWrapper<List<PersonEntity>, MyBasicError>>

    fun addNewPerson(person : PersonEntity) : Flow<ResponseWrapper<Any?, MyBasicError>>

}