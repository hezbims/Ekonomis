package com.hezapp.ekonomis.add_new_transaction.presentation.main_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_new_transaction.data.person.FakePersonRepo
import com.hezapp.ekonomis.add_new_transaction.domain.person.IPersonRepo
import com.hezapp.ekonomis.add_new_transaction.domain.person.PersonEntity
import com.hezapp.ekonomis.add_new_transaction.domain.use_case.GetValidatedPpnFromInputStringUseCase
import com.hezapp.ekonomis.core.data.repo.FakeProductRepo
import com.hezapp.ekonomis.core.domain.entity.relationship.InvoiceItemWithProduct
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.IProductRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddNewTransactionViewModel : ViewModel() {
    private val profileRepo : IPersonRepo = FakePersonRepo()
    private val productRepo : IProductRepo = FakeProductRepo()
    private val getValidPpnFromInput = GetValidatedPpnFromInputStringUseCase()

    private val _state = MutableStateFlow(AddNewTransactionUiState.init())
    val state : StateFlow<AddNewTransactionUiState>
        get() = _state.asStateFlow()

    fun onEvent(event: AddNewTransactionEvent){
        when(event){
            is AddNewTransactionEvent.ChangeTransactionType ->
                changeTransactionType(event.newTransactionType)
            is AddNewTransactionEvent.LoadAvailableProfilesWithSearchQuery ->
                loadAvailableProfilesWithSearchQuery(event.searchQuery)
            is AddNewTransactionEvent.ChooseNewPerson ->
                chooseNewPerson(event.newPerson)
            is AddNewTransactionEvent.CreateNewProfile ->
                createNewProfile(event.profileName)
            AddNewTransactionEvent.DoneHandlingSuccessCreateNewProfile ->
                doneHandlingSuccessCreateNewProfile()
            is AddNewTransactionEvent.ChangeTransactionDate ->
                changeTransactionDate(event.newDate)
            is AddNewTransactionEvent.ChangePpn ->
                changePpn(event.newPpn)
        }
    }

    private fun changeTransactionType(newTransactionType: TransactionType){
        _state.update { it.copy(transactionType = newTransactionType) }
    }

    private fun loadAvailableProfilesWithSearchQuery(newSearchQuery : String){
        viewModelScope.launch(Dispatchers.IO){
            profileRepo.getPersonFiltered(newSearchQuery).collectLatest { response ->
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
                    ProfileType.SUPPLIER
                else ProfileType.CUSTOMER
            )

            profileRepo.addNewPerson(personEntity).collect { response ->
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

    private fun changePpn(inputPpn : String){
        try {
            if (inputPpn.isEmpty())
                _state.update { it.copy(ppn = null) }
            else
                _state.update { it.copy(ppn = getValidPpnFromInput(inputPpn)) }
        } catch (_ : Exception){ }
    }
}

sealed class AddNewTransactionEvent {
    class ChangeTransactionType(val newTransactionType: TransactionType) : AddNewTransactionEvent()
    class ChooseNewPerson(val newPerson : PersonEntity) : AddNewTransactionEvent()
    class LoadAvailableProfilesWithSearchQuery(val searchQuery : String) : AddNewTransactionEvent()
    class CreateNewProfile(val profileName : String) : AddNewTransactionEvent()
    data object DoneHandlingSuccessCreateNewProfile : AddNewTransactionEvent()
    class ChangeTransactionDate(val newDate: Long) : AddNewTransactionEvent()
    class ChangePpn(val newPpn : String) : AddNewTransactionEvent()
}

data class AddNewTransactionUiState(
    val transactionType: TransactionType?,
    val person: PersonEntity?,
    val availablePerson: ResponseWrapper<List<PersonEntity> , MyBasicError>,
    val createNewPersonResponse: ResponseWrapper<Any? , MyBasicError>?,
    val transactionDateMillis : Long?,
    val ppn : Int?,
    val invoiceItems : List<InvoiceItemWithProduct>,
){
    companion object {
        fun init() = AddNewTransactionUiState(
            transactionType = null,
            person = null,
            availablePerson = ResponseWrapper.Loading(),
            createNewPersonResponse = null,
            transactionDateMillis = null,
            ppn = null,
            invoiceItems = emptyList(),
        )
    }
}