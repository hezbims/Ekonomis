package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.CreateOrUpdateInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.GetValidatedPpnFromInputStringUseCase
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.InvoiceItemUiModel
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

class AddOrUpdateTransactionViewModel : ViewModel() {
    private val getValidPpnFromInput = GetValidatedPpnFromInputStringUseCase()
    private val createOrUpdateInvoiceUseCase = CreateOrUpdateInvoiceUseCase()

    private val _state = MutableStateFlow(AddOrUpdateTransactionUiState.init())
    val state : StateFlow<AddOrUpdateTransactionUiState>
        get() = _state.asStateFlow()

    fun onEvent(event: AddOrUpdateTransactionEvent){
        when(event){
            is AddOrUpdateTransactionEvent.ChangeTransactionType ->
                changeTransactionType(event.newTransactionType)
            is AddOrUpdateTransactionEvent.ChangeProfile ->
                changeProfile(event.newProfile)
            is AddOrUpdateTransactionEvent.ChangeTransactionDate ->
                changeTransactionDate(event.newDate)
            is AddOrUpdateTransactionEvent.ChangePpn ->
                changePpn(event.newPpn)
            is AddOrUpdateTransactionEvent.AddNewInvoiceItem ->
                addOrInsertInvoiceItem(item = event.item)
            is AddOrUpdateTransactionEvent.ChooseInvoiceItemForEdit ->
                chooseInvoiceItemForEdit(event.item)
            AddOrUpdateTransactionEvent.CancelEditInvoiceItem ->
                doneEditInvoiceItem()
            is AddOrUpdateTransactionEvent.DeleteInvoiceItem ->
                deleteInvoiceItem(event.uuid)
            is AddOrUpdateTransactionEvent.EditInvoiceItem ->
                editInvoiceItem(event.item)
            AddOrUpdateTransactionEvent.SubmitData ->
                submitData()
            AddOrUpdateTransactionEvent.DoneHandlingSubmitDataResponse ->
                doneHandlingSubmitDataResponse()
            is AddOrUpdateTransactionEvent.UpdateFormError ->
                updateFormError(event.newFormError)
            AddOrUpdateTransactionEvent.DoneShowQuitConfirmationDialog ->
                doneShowQuitConfirmationDialog()
            AddOrUpdateTransactionEvent.ShowQuitConfirmationDialog ->
                showQuitConfirmationDialog()
        }
    }

    private fun changeTransactionType(newTransactionType: TransactionType){
        if (_state.value.transactionType != newTransactionType)
            _state.update {
                AddOrUpdateTransactionUiState.init().copy(transactionType = newTransactionType)
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

    private fun showQuitConfirmationDialog() {
        _state.update { it.copy(showQuitConfirmationDialog = true) }
    }

    private fun doneShowQuitConfirmationDialog(){
        _state.update { it.copy(showQuitConfirmationDialog = false) }
    }
}

sealed class AddOrUpdateTransactionEvent {
    class ChangeTransactionType(val newTransactionType: TransactionType) : AddOrUpdateTransactionEvent()
    class ChangeProfile(val newProfile: ProfileEntity) : AddOrUpdateTransactionEvent()
    class ChangeTransactionDate(val newDate: Long) : AddOrUpdateTransactionEvent()
    class ChangePpn(val newPpn : String) : AddOrUpdateTransactionEvent()
    class AddNewInvoiceItem(val item: InvoiceItemUiModel) : AddOrUpdateTransactionEvent()
    class EditInvoiceItem(val item: InvoiceItemUiModel) : AddOrUpdateTransactionEvent()
    class ChooseInvoiceItemForEdit(val item: InvoiceItemUiModel) : AddOrUpdateTransactionEvent()
    data object CancelEditInvoiceItem : AddOrUpdateTransactionEvent()
    class DeleteInvoiceItem(val uuid: String) : AddOrUpdateTransactionEvent()
    data object SubmitData : AddOrUpdateTransactionEvent()
    data object DoneHandlingSubmitDataResponse : AddOrUpdateTransactionEvent()
    class UpdateFormError(val newFormError: TransactionFormErrorUiModel) : AddOrUpdateTransactionEvent()
    data object ShowQuitConfirmationDialog : AddOrUpdateTransactionEvent()
    data object DoneShowQuitConfirmationDialog : AddOrUpdateTransactionEvent()
}

data class AddOrUpdateTransactionUiState(
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
    val showQuitConfirmationDialog : Boolean = false
){
    companion object {
        fun init() = AddOrUpdateTransactionUiState(
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