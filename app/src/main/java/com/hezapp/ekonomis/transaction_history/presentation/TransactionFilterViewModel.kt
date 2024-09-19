package com.hezapp.ekonomis.transaction_history.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class TransactionFilterViewModel(
    initialState: PreviewTransactionFilter
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
        }

    }

    private fun decrementMonthYear(){
        _state.update {
            val currentMonthYearCalendar = Calendar.getInstance().apply {
                timeInMillis = it.monthYear
            }
            val prevMonthYearCalendar = currentMonthYearCalendar.apply {
                add(Calendar.MONTH, -1)
            }
            it.copy(monthYear = prevMonthYearCalendar.timeInMillis)
        }
    }

    private fun incrementMonthYear(){
        _state.update {
            val currentMonthYearCalendar = Calendar.getInstance().apply {
                timeInMillis = it.monthYear
            }
            val nextMonthYear = currentMonthYearCalendar.apply {
                add(Calendar.MONTH, 1)
            }
            it.copy(monthYear = nextMonthYear.timeInMillis)
        }
    }
}

private class StateSaver : Saver<TransactionFilterViewModel, PreviewTransactionFilter>{
    override fun restore(value: PreviewTransactionFilter): TransactionFilterViewModel {
        return TransactionFilterViewModel(value)
    }

    override fun SaverScope.save(value: TransactionFilterViewModel): PreviewTransactionFilter {
        return value.state.value
    }
}

@Composable
fun rememberTransactionFilterViewModel(
    initialState: PreviewTransactionFilter,
) : TransactionFilterViewModel {
    val viewModel = rememberSaveable(
        saver = StateSaver()
    ){ TransactionFilterViewModel(initialState) }
    return viewModel
}

sealed class TransactionFilterEvent {
    data object IncrementMonthYear : TransactionFilterEvent()
    data object DecrementMonthYear : TransactionFilterEvent()
}