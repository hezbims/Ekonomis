package com.hezapp.ekonomis.add_new_transaction.presentation

import androidx.lifecycle.ViewModel
import com.hezapp.ekonomis.core.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddNewTransactionViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddNewTransactionUiState.init())
    val state : StateFlow<AddNewTransactionUiState>
        get() = _state.asStateFlow()

    fun onEvent(event: AddNewTransactionEvent){
        when(event){
            is AddNewTransactionEvent.ChangeTransactionType ->
                changeTransactionType(event.newTransactionType)
        }
    }

    private fun changeTransactionType(newTransactionType: TransactionType){
        _state.update { it.copy(transactionType = newTransactionType) }
    }
}

sealed class AddNewTransactionEvent {
    class ChangeTransactionType(val newTransactionType: TransactionType) : AddNewTransactionEvent()
}

data class AddNewTransactionUiState(
    val transactionType: TransactionType?
){
    companion object {
        fun init() = AddNewTransactionUiState(
            transactionType = null,
        )
    }
}

