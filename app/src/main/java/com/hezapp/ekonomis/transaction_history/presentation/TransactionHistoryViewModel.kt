package com.hezapp.ekonomis.transaction_history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.transaction_history.domain.use_case.GetPreviewTransactionHistoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionHistoryViewModel(
    private val getPreviewTransactionHistory : GetPreviewTransactionHistoryUseCase,
    timeService : ITimeService,
) : ViewModel() {


    private val _state = MutableStateFlow(TransactionHistoryUiState.initWith(timeService))
    val state : StateFlow<TransactionHistoryUiState> = _state.onStart {
            loadListPreviewTransactionHistory()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionHistoryUiState.initWith(timeService),
        )

    fun onEvent(event : TransactionHistoryEvent){
        when(event){
            TransactionHistoryEvent.LoadListPreviewTransactionHistory ->
                loadListPreviewTransactionHistory()
            TransactionHistoryEvent.DoneNavigateToTransactionDetail ->
                doneNavigateToTransactionDetail()
            TransactionHistoryEvent.DismissFilterBottomSheet ->
                dismissFilterBottomSheet()
            TransactionHistoryEvent.ShowFilterBottomSheet ->
                showFilterBottomSheet()
            is TransactionHistoryEvent.ChangeFilter ->
                changeFilter(newFilter = event.newFilter)
        }
    }

    private fun loadListPreviewTransactionHistory(){
        viewModelScope.launch(Dispatchers.IO){
            getPreviewTransactionHistory(_state.value.filterState).collect { response ->
                _state.update {
                    it.copy(transactionHistoryResponse = response)
                }
            }
        }
    }

    private fun doneNavigateToTransactionDetail(){
        _state.update {
            it.copy(navigateToTransactionDetail = null)
        }
    }

    private fun dismissFilterBottomSheet(){
        _state.update { it.copy(showFilterBottomSheet = false) }
    }

    private fun showFilterBottomSheet(){
        _state.update { it.copy(showFilterBottomSheet = true) }
    }

    private fun changeFilter(newFilter: PreviewTransactionFilter){
        _state.update {
            it.copy(
                filterState = newFilter,
                showFilterBottomSheet = false,
            )
        }
        loadListPreviewTransactionHistory()
    }
}

data class TransactionHistoryUiState(
    val transactionHistoryResponse : ResponseWrapper<List<PreviewTransactionHistory> , MyBasicError> = ResponseWrapper.Loading(),
    val navigateToTransactionDetail : Int? = null,
    val filterState: PreviewTransactionFilter,
    val showFilterBottomSheet : Boolean = false,
){
    companion object {
        fun initWith(timeService: ITimeService) =
            TransactionHistoryUiState(
                filterState = PreviewTransactionFilter(timeService.getCalendar().apply {
                    set(Calendar.DAY_OF_MONTH , 1)
                    set(Calendar.HOUR_OF_DAY , 0)
                    set(Calendar.MINUTE , 0)
                    set(Calendar.SECOND , 0)
                    set(Calendar.MILLISECOND , 0)
                }.timeInMillis)
            )
    }
}

sealed class TransactionHistoryEvent {
    data object LoadListPreviewTransactionHistory : TransactionHistoryEvent()
    data object DoneNavigateToTransactionDetail : TransactionHistoryEvent()
    data object ShowFilterBottomSheet : TransactionHistoryEvent()
    data object DismissFilterBottomSheet : TransactionHistoryEvent()
    class ChangeFilter(val newFilter : PreviewTransactionFilter) : TransactionHistoryEvent()
}