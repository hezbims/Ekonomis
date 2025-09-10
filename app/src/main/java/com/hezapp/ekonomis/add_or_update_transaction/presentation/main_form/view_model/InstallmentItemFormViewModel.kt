package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.view_model

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.dto.InstallmentItemUiDto
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.presentation.utils.InputTextToNonNegativeRupiahTransformer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

class InstallmentItemFormViewModel(
    initialState: InstallmentItemFormUiState,
) {
    constructor(timeService: ITimeService, dto: InstallmentItemUiDto?) :
        this(InstallmentItemFormUiState.initWith(timeService, dto))

    private val _state = MutableStateFlow(initialState)
    val state : StateFlow<InstallmentItemFormUiState>
        get() = _state
    private val _popBackEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val popBackEvent = _popBackEvent.asSharedFlow()

    fun changeDate(newDate: LocalDate){
        _state.update { it.copy(date = newDate) }
    }

    private val inputTextToNonNegativeRupiahTransformer = InputTextToNonNegativeRupiahTransformer()
    fun changeAmount(newAmount: String){
        _state.update {
            val newAmount = inputTextToNonNegativeRupiahTransformer(
                inputText = newAmount,
                defaultValue = it.amount)
            if (newAmount != it.amount)
                it.copy(
                    amount = newAmount,
                    amountHasError = false,
                )
            else it
        }
    }

    fun validateForm() {
        if (state.value.amount.let {
                it == null || it == 0
            })
            _state.update {
                it.copy(amountHasError = true)
            }
        else {
            _popBackEvent.tryEmit(Unit)
        }
    }
}

@Composable
fun rememberInstallmentItemFormViewModel(
    timeService : ITimeService,
    initialData: InstallmentItemUiDto?,
) = rememberSaveable(
    saver = getSaver()
) {
    InstallmentItemFormViewModel(timeService, initialData)
}

private fun getSaver() = Saver<InstallmentItemFormViewModel, InstallmentItemFormUiState>(
    save = { it.state.value },
    restore = { InstallmentItemFormViewModel(it) }
)

@Parcelize
data class InstallmentItemFormUiState(
    val date: LocalDate,
    val amount: Int?,
    val amountHasError: Boolean = false,
) : Parcelable {
    companion object {
        fun initWith(
            timeService: ITimeService,
            dto: InstallmentItemUiDto?,
        ) = InstallmentItemFormUiState(
            date = dto?.date ?: timeService.getLocalDate(),
            amount = dto?.amount,
        )
    }
}