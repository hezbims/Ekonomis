package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceFormModel
import com.hezapp.ekonomis.add_or_update_transaction.domain.model.InvoiceValidationResult
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.CreateOrUpdateInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.GetFullInvoiceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.GetValidatedPpnFromInputStringUseCase
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.InvoiceItemUiModel
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.toUiModel
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice.relationship.FullInvoiceDetails
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddOrUpdateTransactionViewModel(invoiceId : Int?) : ViewModel() {
    private val getValidPpnFromInput = GetValidatedPpnFromInputStringUseCase()
    private val createOrUpdateInvoiceUseCase = CreateOrUpdateInvoiceUseCase()
    private val getFullInvoice = GetFullInvoiceUseCase()

    private val _state = MutableStateFlow(AddOrUpdateTransactionUiState())
    val state : StateFlow<AddOrUpdateTransactionUiState> = _state
        .onStart {
            loadPreviousInvoice(invoiceId = invoiceId)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AddOrUpdateTransactionUiState().let {
                it.copy(curFormData = it.curFormData.copy(id = invoiceId ?: 0))
            },
        )

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

    private fun loadPreviousInvoice(invoiceId: Int?){
        if (invoiceId == null){
            _state.update { it.copy(prevFormData = ResponseWrapper.Succeed(
                data = TransactionUiFormDataModel.initNew()
            )) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            getFullInvoice(invoiceId).collect { response ->
                _state.update {
                    when(response){
                        is ResponseWrapper.Failed -> it.copy(prevFormData = ResponseWrapper.Failed())
                        is ResponseWrapper.Loading -> it.copy(prevFormData = ResponseWrapper.Loading())
                        is ResponseWrapper.Succeed -> {
                            val uiModel = TransactionUiFormDataModel.fromFullInvoiceDetails(response.data)
                            it.copy(
                                prevFormData = ResponseWrapper.Succeed(uiModel),
                                curFormData = uiModel
                            )
                        }
                    }
                }
            }
        }
    }

    private fun changeTransactionType(newTransactionType: TransactionType){
        if (_state.value.curFormData.transactionType != newTransactionType)
            _state.update {
                it.copy(
                    curFormData = TransactionUiFormDataModel.initNew().copy(
                        id = it.curFormData.id,
                        transactionType = newTransactionType,
                    )
                )
            }
    }

    private fun changeProfile(newProfile : ProfileEntity){
        _state.update {
            it.copy(
                curFormData = it.curFormData.copy(profile = newProfile),
                formError = it.formError.copy(profileError = null),
            )
        }
    }

    private fun changeTransactionDate(newDate: Long){
        _state.update {
            it.copy(
                curFormData = it.curFormData.copy(transactionDateMillis = newDate),
                formError = it.formError.copy(transactionDateError = null)
            )
        }
    }

    private fun changePpn(inputPpn : String){
        try {
            if (inputPpn.isEmpty())
                _state.update {
                    it.copy(
                        curFormData = it.curFormData.copy(ppn = null),
                        formError = it.formError.copy(ppnError = null),
                    )
                }
            else {
                _state.update {
                    it.copy(
                        curFormData = it.curFormData.copy(
                            ppn = getValidPpnFromInput(inputPpn)
                        ),
                        formError = it.formError.copy(ppnError = null)
                    )
                }
            }
        } catch (_ : Exception){ }
    }

    private fun addOrInsertInvoiceItem(
        item: InvoiceItemUiModel,
    ){
        _state.update {
            it.copy(
                curFormData = it.curFormData.copy(
                    invoiceItems = it.curFormData.invoiceItems.plus(item)
                ),
                formError = it.formError.copy(invoiceItemsError = null)
            )
        }
    }

    private fun editInvoiceItem(updateItem: InvoiceItemUiModel){
        _state.update {
            val prevInvoiceItems = it.curFormData.invoiceItems
            val newFormData = it.curFormData.copy(
                invoiceItems = prevInvoiceItems.map { prevItem ->
                    if (updateItem.listId == prevItem.listId)
                        updateItem
                    else
                        prevItem

                }
            )
            it.copy(
                curFormData = newFormData,
                editInvoiceItem = null,
            )
        }
    }

    private fun chooseInvoiceItemForEdit(item: InvoiceItemUiModel){
        _state.update { it.copy(editInvoiceItem = item) }
    }

    private fun doneEditInvoiceItem(){
        _state.update { it.copy(editInvoiceItem = null) }
    }

    private fun deleteInvoiceItem(uuid: String){
        _state.update {
            val newInvoiceItems = it.curFormData.invoiceItems.filter { item ->
                item.listId != uuid
            }

            it.copy(
                curFormData = it.curFormData.copy(invoiceItems = newInvoiceItems),
                editInvoiceItem = null,
            )
        }
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

    class Factory(private val invoiceId: Int?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(AddOrUpdateTransactionViewModel::class.java))
                return AddOrUpdateTransactionViewModel(invoiceId = invoiceId) as T
            throw IllegalArgumentException()
        }
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
    val curFormData : TransactionUiFormDataModel = TransactionUiFormDataModel.initNew(),
    val prevFormData : ResponseWrapper<TransactionUiFormDataModel , MyBasicError> = ResponseWrapper.Loading(),
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
    fun toInvoiceFormModel() : InvoiceFormModel =
        InvoiceFormModel(
            id = curFormData.id,
            ppn = curFormData.ppn,
            prevInvoiceItems = prevFormData.asSucceed().data.invoiceItems.map { it.toInvoiceItemEntity(it.id) },
            newInvoiceItems = curFormData.invoiceItems.map { it.toInvoiceItemEntity(invoiceId = it.id) },
            profile = curFormData.profile,
            transactionDateMillis = curFormData.transactionDateMillis,
            transactionType = curFormData.transactionType,
        )

    val isFormDataEdited : Boolean
        get() =
            when(prevFormData){
                is ResponseWrapper.Failed -> false
                is ResponseWrapper.Loading -> false
                is ResponseWrapper.Succeed -> prevFormData.data != curFormData
            }
}

data class TransactionUiFormDataModel(
    val id: Int,
    val transactionType: TransactionType?,
    val profile: ProfileEntity?,
    val transactionDateMillis : Long?,
    val ppn : Int?,
    val invoiceItems : List<InvoiceItemUiModel>,
){
    companion object {
        fun initNew() : TransactionUiFormDataModel =
            TransactionUiFormDataModel(
                id = 0,
                transactionType = null,
                profile = null,
                transactionDateMillis = null,
                ppn = null,
                invoiceItems = emptyList(),
            )

        fun fromFullInvoiceDetails(invoiceDetails : FullInvoiceDetails) : TransactionUiFormDataModel =
            TransactionUiFormDataModel(
                id = invoiceDetails.invoice.invoice.id,
                transactionType = invoiceDetails.invoice.invoice.transactionType,
                profile = invoiceDetails.profile,
                transactionDateMillis = invoiceDetails.invoice.invoice.date,
                ppn = invoiceDetails.invoice.invoice.ppn,
                invoiceItems = invoiceDetails.invoice.invoiceItemWithProducts.map {
                    it.toUiModel()
                }
            )
    }
}

data class TransactionFormErrorUiModel(
    val profileError: String?,
    val transactionDateError: String?,
    val ppnError : String?,
    val invoiceItemsError : String?,
)