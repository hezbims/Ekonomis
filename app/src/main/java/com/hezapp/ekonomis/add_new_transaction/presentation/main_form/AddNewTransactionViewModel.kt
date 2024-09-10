package com.hezapp.ekonomis.add_new_transaction.presentation.main_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_new_transaction.data.person.FakePersonRepo
import com.hezapp.ekonomis.add_new_transaction.domain.person.IPersonRepo
import com.hezapp.ekonomis.add_new_transaction.domain.person.PersonEntity
import com.hezapp.ekonomis.add_new_transaction.domain.use_case.GetValidatedPpnFromInputStringUseCase
import com.hezapp.ekonomis.add_new_transaction.presentation.model.InvoiceItemUiModel
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddNewTransactionViewModel : ViewModel() {
    private val profileRepo : IPersonRepo = FakePersonRepo()
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
            is AddNewTransactionEvent.AddNewInvoiceItem ->
                addOrInsertInvoiceItem(item = event.item)
            is AddNewTransactionEvent.ChooseInvoiceItemForEdit ->
                chooseInvoiceItemForEdit(event.item)
            AddNewTransactionEvent.CancelEditInvoiceItem ->
                doneEditInvoiceItem()
            is AddNewTransactionEvent.DeleteInvoiceItem ->
                deleteInvoiceItem(event.uuid)
            is AddNewTransactionEvent.EditInvoiceItem ->
                editInvoiceItem(event.item)
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

    private fun addOrInsertInvoiceItem(
        item: InvoiceItemUiModel,
    ){
        val prevInvoiceItems = _state.value.invoiceItems
        _state.update { it.copy(invoiceItems = prevInvoiceItems.plus(item)) }
    }

    private fun editInvoiceItem(updateItem: InvoiceItemUiModel){
        val prevInvoiceItems = _state.value.invoiceItems
        _state.update { it.copy(invoiceItems =
            prevInvoiceItems.map { prevItem ->
                if (updateItem.listId == prevItem.listId)
                    updateItem
                else
                    prevItem
            },
            editInvoiceItem = null,
        ) }
    }

    private fun chooseInvoiceItemForEdit(item: InvoiceItemUiModel){
        _state.update { it.copy(editInvoiceItem = item) }
    }

    private fun doneEditInvoiceItem(){
        _state.update { it.copy(editInvoiceItem = null) }
    }

    private fun deleteInvoiceItem(uuid: String){
        val newInvoiceItems = _state.value.invoiceItems.filter { item ->
            item.listId != uuid
        }

        _state.update { it.copy(
            invoiceItems = newInvoiceItems,
            editInvoiceItem = null,
        ) }
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
    class AddNewInvoiceItem(val item: InvoiceItemUiModel) : AddNewTransactionEvent()
    class EditInvoiceItem(val item: InvoiceItemUiModel) : AddNewTransactionEvent()
    class ChooseInvoiceItemForEdit(val item: InvoiceItemUiModel) : AddNewTransactionEvent()
    data object CancelEditInvoiceItem : AddNewTransactionEvent()
    class DeleteInvoiceItem(val uuid: String) : AddNewTransactionEvent()
}

data class AddNewTransactionUiState(
    val transactionType: TransactionType?,
    val person: PersonEntity?,
    val availablePerson: ResponseWrapper<List<PersonEntity> , MyBasicError>,
    val createNewPersonResponse: ResponseWrapper<Any? , MyBasicError>?,
    val transactionDateMillis : Long?,
    val ppn : Int?,
    val invoiceItems : List<InvoiceItemUiModel>,
    val editInvoiceItem: InvoiceItemUiModel?,
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
            editInvoiceItem = null,
        )
    }
}