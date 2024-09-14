package com.hezapp.ekonomis.add_new_transaction.presentation.main_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_new_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.add_new_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.add_new_transaction.domain.use_case.CreateOrUpdateInvoiceUseCase
import com.hezapp.ekonomis.add_new_transaction.domain.use_case.GetValidatedPpnFromInputStringUseCase
import com.hezapp.ekonomis.add_new_transaction.presentation.model.InvoiceItemUiModel
import com.hezapp.ekonomis.core.domain.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddNewTransactionViewModel : ViewModel() {
    private val getValidPpnFromInput = GetValidatedPpnFromInputStringUseCase()
    private val createOrUpdateInvoiceUseCase = CreateOrUpdateInvoiceUseCase()

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
            AddNewTransactionEvent.SubmitData ->
                submitData()
            AddNewTransactionEvent.DoneHandlingSubmitDataResponse ->
                doneHandlingSubmitDataResponse()
            is AddNewTransactionEvent.UpdateFormError ->
                updateFormError(event.newFormError)
        }
    }

    private fun changeTransactionType(newTransactionType: TransactionType){
        if (_state.value.transactionType != newTransactionType)
            _state.update {
                AddNewTransactionUiState.init().copy(transactionType = newTransactionType)
            }
    }

    private fun changeProfile(newProfile : ProfileEntity){
        _state.update {
            val nextError = it.formError.copy(profileError = null)
            it.copy(profile = newProfile, formError = nextError)
        }
    }

    private fun changeTransactionDate(newDate: Long){
        _state.update {
            val nextError = it.formError.copy(transactionDateError = null)
            it.copy(transactionDateMillis = newDate, formError = nextError)
        }
    }

    private fun changePpn(inputPpn : String){
        try {
            if (inputPpn.isEmpty())
                _state.update { it.copy(ppn = null) }
            else {
                _state.update {
                    val nextError = it.formError.copy(ppnError = null)
                    it.copy(ppn = getValidPpnFromInput(inputPpn), formError = nextError)
                }
            }
        } catch (_ : Exception){ }
    }

    private fun addOrInsertInvoiceItem(
        item: InvoiceItemUiModel,
    ){
        val prevInvoiceItems = _state.value.invoiceItems
        _state.update {
            val nextError = it.formError.copy(invoiceItemsError = null)
            it.copy(invoiceItems = prevInvoiceItems.plus(item), formError = nextError)
        }
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

    private fun submitData(){
        viewModelScope.launch(Dispatchers.IO) {
            createOrUpdateInvoiceUseCase(invoiceForm = _state.value.toInvoiceFormModel()).collect { response ->
                _state.update { it.copy(submitResponse = response) }
            }
        }
    }

    private fun doneHandlingSubmitDataResponse(){
        _state.update { it.copy(submitResponse = null) }
    }

    private fun updateFormError(newFormError: TransactionFormErrorUiModel){
        _state.update { it.copy(formError = newFormError) }
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
    data object SubmitData : AddNewTransactionEvent()
    data object DoneHandlingSubmitDataResponse : AddNewTransactionEvent()
    class UpdateFormError(val newFormError: TransactionFormErrorUiModel) : AddNewTransactionEvent()
}

data class AddNewTransactionUiState(
    val id: Int,
    val transactionType: TransactionType?,
    val profile: ProfileEntity?,
    val transactionDateMillis : Long?,
    val ppn : Int?,
    val invoiceItems : List<InvoiceItemUiModel> = emptyList(),
    val prevInvoiceItems: List<InvoiceItemEntity>,
    val editInvoiceItem: InvoiceItemUiModel? = null,
    val submitResponse: ResponseWrapper<Any?, InvoiceValidationResult>? = null,
    val formError: TransactionFormErrorUiModel = TransactionFormErrorUiModel(
        transactionDateError = null,
        invoiceItemsError = null,
        profileError = null,
        ppnError = null,
    ),
){
    companion object {
        fun init() = AddNewTransactionUiState(
            id = 0,
            transactionType = null,
            profile = null,
            transactionDateMillis = null,
            ppn = null,
            prevInvoiceItems = emptyList(),
        )
    }

    fun toInvoiceFormModel() : InvoiceFormModel =
        InvoiceFormModel(
            id = id,
            ppn = ppn,
            prevInvoiceItems = prevInvoiceItems,
            newInvoiceItems = invoiceItems.map { it.toInvoiceItemEntity(id) },
            profile = profile,
            transactionDateMillis = transactionDateMillis,
            transactionType = transactionType,
        )
}

data class TransactionFormErrorUiModel(
    val profileError: String?,
    val transactionDateError: String?,
    val ppnError : String?,
    val invoiceItemsError : String?,
)