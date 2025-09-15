package com.hezapp.ekonomis.transaction_history.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.core.domain.utils.getPreviousMonthYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TransactionFilterViewModel(
    initialState: PreviewTransactionFilter,
    private val timeService: ITimeService,
) {
    private val _state = MutableStateFlow(initialState)
    val state : StateFlow<PreviewTransactionFilter>
        get() = _state.asStateFlow()

    fun onEvent(event : TransactionFilterEvent){
        when(event){
            TransactionFilterEvent.DecrementMonthYear ->
                decrementMonthYear()
            TransactionFilterEvent.IncrementMonthYear ->
                incrementMonthYear()
            is TransactionFilterEvent.ChangeOnlyNotPaidOffFilter ->
                changeOnlyNotPaidOffFilter(event.newValue)
        }
    }

    private fun changeOnlyNotPaidOffFilter(newValue: Boolean){
        _state.update {
            it.copy(isOnlyNotPaidOff = newValue)
        }
    }

    private fun decrementMonthYear(){
        _state.update {
            it.copy(monthYear = it.monthYear.getPreviousMonthYear(timeService))
        }
    }

    private fun incrementMonthYear(){
        _state.update {
            it.copy(monthYear = it.monthYear.getNextMonthYear(timeService))
        }
    }
}

private class StateSaver(private val timeService: ITimeService) : Saver<TransactionFilterViewModel, PreviewTransactionFilter>{
    override fun restore(value: PreviewTransactionFilter): TransactionFilterViewModel {
        return TransactionFilterViewModel(value, timeService)
    }

    override fun SaverScope.save(value: TransactionFilterViewModel): PreviewTransactionFilter {
        return value.state.value
    }
}

@Composable
fun rememberTransactionFilterViewModel(
    initialState: PreviewTransactionFilter,
    timeService: ITimeService,
) : TransactionFilterViewModel {
    val viewModel = rememberSaveable(
        saver = StateSaver(timeService)
    ){ TransactionFilterViewModel(initialState, timeService) }
    return viewModel
}

sealed class TransactionFilterEvent {
    data object IncrementMonthYear : TransactionFilterEvent()
    data object DecrementMonthYear : TransactionFilterEvent()
    class ChangeOnlyNotPaidOffFilter(val newValue: Boolean) : TransactionFilterEvent()
}