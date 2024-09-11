package com.hezapp.ekonomis.add_new_transaction.presentation.main_form

import androidx.lifecycle.ViewModel
import com.hezapp.ekonomis.add_new_transaction.domain.use_case.GetValidatedPpnFromInputStringUseCase
import com.hezapp.ekonomis.add_new_transaction.presentation.model.InvoiceItemUiModel
import com.hezapp.ekonomis.core.domain.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddNewTransactionViewModel : ViewModel() {
    private val getValidPpnFromInput = GetValidatedPpnFromInputStringUseCase()

    private val _state = MutableStateFlow(AddNewTransactionUiState.init())
    val state : StateFlow<AddNewTransactionUiState>
        get() = _state.asStateFlow()

    fun onEvent(event: AddNewTransactionEvent){
        when(event){
            is AddNewTransactionEvent.ChangeTransactionType ->
                changeTransactionType(event.newTransactionType)
            is AddNewTransactionEvent.ChangeProfile ->
                changeProfile(event.newProfile)
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
        if (_state.value.transactionType != newTransactionType)
            _state.update {
                AddNewTransactionUiState.init().copy(transactionType = newTransactionType)
            }
    }

    private fun changeProfile(newProfile : ProfileEntity){
        _state.update { it.copy(profile = newProfile) }
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
    class ChangeProfile(val newProfile: ProfileEntity) : AddNewTransactionEvent()
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
    val profile: ProfileEntity?,
    val createNewPersonResponse: ResponseWrapper<Any? , MyBasicError>?,
    val transactionDateMillis : Long?,
    val ppn : Int?,
    val invoiceItems : List<InvoiceItemUiModel>,
    val editInvoiceItem: InvoiceItemUiModel?,
){
    companion object {
        fun init() = AddNewTransactionUiState(
            transactionType = null,
            profile = null,
            createNewPersonResponse = null,
            transactionDateMillis = null,
            ppn = null,
            invoiceItems = emptyList(),
            editInvoiceItem = null,
        )
    }
}