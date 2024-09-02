package com.hezapp.ekonomis.transaction_history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.transaction_history.data.service.FakePreviewTransactionHistoryRepo
import com.hezapp.ekonomis.transaction_history.domain.model.PreviewTransactionHistory
import com.hezapp.ekonomis.transaction_history.domain.service.IPreviewTransactionHistoryRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionHistoryViewModel : ViewModel() {
    private val repo : IPreviewTransactionHistoryRepo = FakePreviewTransactionHistoryRepo()

    private val _state = MutableStateFlow(TransactionHistoryUiState.init())
    val state : StateFlow<TransactionHistoryUiState>
        get() = _state.asStateFlow()


    init {
        loadListPreviewTransactionHistory()
    }

    fun onEvent(event : TransactionHistoryEvent){
        when(event){
            TransactionHistoryEvent.LoadListPreviewTransactionHistory ->
                loadListPreviewTransactionHistory()
            TransactionHistoryEvent.DoneNavigateToTransactionDetail ->
                doneNavigateToTransactionDetail()
            is TransactionHistoryEvent.NavigateToTransactionDetail ->
                navigateToTransactionDetail(event.transactionId)
        }
    }

    private fun loadListPreviewTransactionHistory(){
        viewModelScope.launch(Dispatchers.IO){
            repo.getListPreviewTransactionHistory().collect { response ->
                _state.update {
                    it.copy(transactionHistoryResponse = response)
                }
            }
        }
    }

    private fun navigateToTransactionDetail(transactionId : Int){
        _state.update {
            it.copy(navigateToTransactionDetail = transactionId)
        }
    }

    private fun doneNavigateToTransactionDetail(){
        _state.update {
            it.copy(navigateToTransactionDetail = null)
        }
    }
}

data class TransactionHistoryUiState(
    val transactionHistoryResponse : ResponseWrapper<List<PreviewTransactionHistory> , MyBasicError>,
    val navigateToTransactionDetail : Int?
){
    companion object {
        fun init() = TransactionHistoryUiState(
            transactionHistoryResponse = ResponseWrapper.Loading(),
            navigateToTransactionDetail = null,
        )
    }
}

sealed class TransactionHistoryEvent {
    data object LoadListPreviewTransactionHistory : TransactionHistoryEvent()
    class NavigateToTransactionDetail(val transactionId : Int) : TransactionHistoryEvent()
    data object DoneNavigateToTransactionDetail : TransactionHistoryEvent()
}