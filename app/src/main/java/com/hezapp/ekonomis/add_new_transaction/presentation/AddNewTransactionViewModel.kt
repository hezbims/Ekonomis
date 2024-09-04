package com.hezapp.ekonomis.add_new_transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_new_transaction.data.person.FakePersonRepo
import com.hezapp.ekonomis.add_new_transaction.domain.person.IPersonRepo
import com.hezapp.ekonomis.add_new_transaction.domain.person.PersonEntity
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.PersonType
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddNewTransactionViewModel : ViewModel() {
    private val repo : IPersonRepo = FakePersonRepo()

    private val _state = MutableStateFlow(AddNewTransactionUiState.init())
    val state : StateFlow<AddNewTransactionUiState>
        get() = _state.asStateFlow()

    fun onEvent(event: AddNewTransactionEvent){
        when(event){
            is AddNewTransactionEvent.ChangeTransactionType ->
                changeTransactionType(event.newTransactionType)
            is AddNewTransactionEvent.ChangeSearchQuery ->
                changeSearchQuery(event.searchQuery)
            is AddNewTransactionEvent.ChooseNewPerson ->
                chooseNewPerson(event.newPerson)
            is AddNewTransactionEvent.CreateNewProfile ->
                createNewProfile(event.profileName)
            AddNewTransactionEvent.DoneHandlingSuccessCreateNewProfile ->
                doneHandlingSuccessCreateNewProfile()
            is AddNewTransactionEvent.ChangeTransactionDate ->
                changeTransactionDate(event.newDate)
        }
    }

    private fun changeTransactionType(newTransactionType: TransactionType){
        _state.update { it.copy(transactionType = newTransactionType) }
    }

    private fun changeSearchQuery(newSearchQuery : String){
        viewModelScope.launch(Dispatchers.IO){
            repo.getPersonFiltered(newSearchQuery).collectLatest { response ->
                _state.update { it.copy(availablePerson = response) }
            }
        }
    }

    private fun chooseNewPerson(newPerson : PersonEntity){
        _state.update { it.copy(person = newPerson) }
    }

    private fun createNewProfile(profileName : String){
        viewModelScope.launch(Dispatchers.IO){
            val personEntity = PersonEntity(
                name = profileName,
                type =
                if (state.value.transactionType == TransactionType.PEMBELIAN)
                    PersonType.SUPPLIER
                else PersonType.CUSTOMER
            )

            repo.addNewPerson(personEntity).collect { response ->
                _state.update { it.copy(createNewPersonResponse = response) }
            }
        }
    }

    private fun doneHandlingSuccessCreateNewProfile(){
        _state.update { it.copy(createNewPersonResponse = null) }
    }

    private fun changeTransactionDate(newDate: Long){
        _state.update { it.copy(transactionDateMillis = newDate) }
    }
}

sealed class AddNewTransactionEvent {
    class ChangeTransactionType(val newTransactionType: TransactionType) : AddNewTransactionEvent()
    class ChooseNewPerson(val newPerson : PersonEntity) : AddNewTransactionEvent()
    class ChangeSearchQuery(val searchQuery : String) : AddNewTransactionEvent()
    class CreateNewProfile(val profileName : String) : AddNewTransactionEvent()
    object DoneHandlingSuccessCreateNewProfile : AddNewTransactionEvent()
    class ChangeTransactionDate(val newDate: Long) : AddNewTransactionEvent()
}

data class AddNewTransactionUiState(
    val transactionType: TransactionType?,
    val person: PersonEntity?,
    val availablePerson: ResponseWrapper<List<PersonEntity> , MyBasicError>,
    val createNewPersonResponse: ResponseWrapper<Object? , MyBasicError>?,
    val transactionDateMillis : Long?,
){
    companion object {
        fun init() = AddNewTransactionUiState(
            transactionType = null,
            person = null,
            availablePerson = ResponseWrapper.Loading(),
            createNewPersonResponse = null,
            transactionDateMillis = null,
        )
    }
}

