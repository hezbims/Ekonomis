package com.hezapp.ekonomis.edit_product_name_dialog.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.EditProductNameError
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.iface.IEditProductNameUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProductNameDialogViewModel(
    private val productId: Int,
    private val editProductName: IEditProductNameUseCase,
) : ViewModel() {

    private val _oneTimeEvent = Channel<EditProductNameDialogOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()

    private val _state = MutableStateFlow(EditProductNameDialogUiState())
    val state: StateFlow<EditProductNameDialogUiState> = _state.asStateFlow()

    fun onEvent(event: EditProductNameDialogEvent) {
        when (event) {
            is EditProductNameDialogEvent.ChangeName ->
                _state.update { it.copy(nameInput = event.name, submitResponse = null) }
            EditProductNameDialogEvent.Submit -> submit()
        }
    }

    private fun submit() {
        viewModelScope.launch(Dispatchers.IO) {
            editProductName(productId = productId, name = _state.value.nameInput)
                .collect { response ->
                    if (response is ResponseWrapper.Succeed)
                        _oneTimeEvent.send(EditProductNameDialogOneTimeEvent.EditSucceed)
                    _state.update { it.copy(submitResponse = response) }
                }
        }
    }
}

data class EditProductNameDialogUiState(
    val nameInput: String = "",
    val submitResponse: ResponseWrapper<Any?, EditProductNameError>? = null,
)

sealed class EditProductNameDialogEvent {
    class ChangeName(val name: String) : EditProductNameDialogEvent()
    data object Submit : EditProductNameDialogEvent()
}

sealed class EditProductNameDialogOneTimeEvent {
    data object EditSucceed : EditProductNameDialogOneTimeEvent()
}