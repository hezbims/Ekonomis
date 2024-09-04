package com.hezapp.ekonomis.add_new_transaction.data.person

import com.hezapp.ekonomis.add_new_transaction.domain.person.IPersonRepo
import com.hezapp.ekonomis.add_new_transaction.domain.person.PersonEntity
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.PersonType
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePersonRepo : IPersonRepo {
    override fun getPersonFiltered(
        personName: String,
        personType: PersonType?
    ): Flow<ResponseWrapper<List<PersonEntity>, MyBasicError>> {
        return flow {
            emit(ResponseWrapper.Loading())
            delay(100L)
            val filteredPerson = listPerson.filter {
                it.name.contains(personName, ignoreCase = true)
            }
            emit(ResponseWrapper.Succeed(filteredPerson))
        }
    }

    override fun addNewPerson(person: PersonEntity): Flow<ResponseWrapper<Object?, MyBasicError>> = flow {
        emit(ResponseWrapper.Loading())
        delay(100L)
        listPerson.add(person.copy(id = listPerson.size + 1))
        emit(ResponseWrapper.Succeed(null))
    }

    private companion object {
        val listPerson = mutableListOf<PersonEntity>(
            PersonEntity(
                id = 1,
                name = "Beni",
                type = PersonType.SUPPLIER
            ),
            PersonEntity(
                id = 2,
                name = "Feni",
                type = PersonType.SUPPLIER
            ),
            PersonEntity(
                id = 3,
                name = "Komang",
                type = PersonType.SUPPLIER
            ),
        )
    }
}