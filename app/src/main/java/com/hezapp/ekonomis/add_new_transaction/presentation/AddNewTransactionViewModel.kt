package com.hezapp.ekonomis.add_new_transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_new_transaction.data.person.FakePersonRepo
import com.hezapp.ekonomis.add_new_transaction.domain.person.IPersonRepo
import com.hezapp.ekonomis.add_new_transaction.domain.person.PersonEntity
import com.hezapp.ekonomis.core.domain.model.MyBasicError
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
}

sealed class AddNewTransactionEvent {
    class ChangeTransactionType(val newTransactionType: TransactionType) : AddNewTransactionEvent()
    class ChooseNewPerson(val newPerson : PersonEntity) : AddNewTransactionEvent()
    class ChangeSearchQuery(val searchQuery : String) : AddNewTransactionEvent()
}

data class AddNewTransactionUiState(
    val transactionType: TransactionType?,
    val person: PersonEntity?,
    val availablePerson: ResponseWrapper<List<PersonEntity> , MyBasicError>
){
    companion object {
        fun init() = AddNewTransactionUiState(
            transactionType = null,
            person = null,
            availablePerson = ResponseWrapper.Loading()
        )
    }
}

