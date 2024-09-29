package com.hezapp.ekonomis.product_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.utils.toBeginningOfMonth
import com.hezapp.ekonomis.core.domain.utils.toCalendar
import com.hezapp.ekonomis.product_detail.domain.model.EditMonthlyStockFieldError
import com.hezapp.ekonomis.product_detail.domain.model.EditMonthlyStockValidationResult
import com.hezapp.ekonomis.product_detail.domain.use_case.EditMonthlyStockUseCase
import com.hezapp.ekonomis.product_detail.domain.use_case.GetLatestPreviousMonthStock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditMonthlyStockDialogViewModel(
    params: Params,
    private val getLatestPreviousMonthStock: GetLatestPreviousMonthStock,
    private val editMonthlyStock: EditMonthlyStockUseCase,
)  : ViewModel() {
    data class Params(
        val quantityPerUnitType : QuantityPerUnitType,
        val period: Long,
        val productId: Int,
        val monthlyStockId: Int,
    )

    private val productId = params.productId
    private val monthlyStockId = params.monthlyStockId
    private val period = params.period.toCalendar().toBeginningOfMonth().timeInMillis
    private val _state = MutableStateFlow(
        SetMonthlyStockDialogUiState(
            quantityResponse = ResponseWrapper.Succeed(
                QuantityField.from(params.quantityPerUnitType)
            )
        )
    )
    val state : StateFlow<SetMonthlyStockDialogUiState>
        get() = _state.asStateFlow()

    fun onEvent(event: EditMonthlyStockDialogEvent){
        when(event){
            is EditMonthlyStockDialogEvent.ChangeCartonQuantity ->
                changeCartonQuantity(event.newValue)
            is EditMonthlyStockDialogEvent.ChangePieceQuantity ->
                changePieceQuantity(event.newValue)
            EditMonthlyStockDialogEvent.ChangeUseFromPreviousMonthStock ->
                changeUseFromPreviousMonth()
            EditMonthlyStockDialogEvent.DoneHandlingSaveMonthlyStockResponse ->
                doneHandlingSaveMonthlyStockResponse()
            EditMonthlyStockDialogEvent.SaveMonthlyStock ->
                saveMonthlyStock()
        }
    }

    private fun changeCartonQuantity(newValue: String){
        _state.update { curState ->
            if (curState.quantityResponse !is ResponseWrapper.Succeed)
                return@update curState


            val newQuantity = try {
                getValidatedQuantity(newValue = newValue)
            } catch (e : IllegalArgumentException){
                curState.quantityResponse.data.cartonQuantity
            }
            curState.copy(
                quantityResponse = ResponseWrapper.Succeed(
                    curState.quantityResponse.data.copy(cartonQuantity = newQuantity)
                ),
                cartonError = null,
            )
        }
    }

    private fun changePieceQuantity(newValue: String){
        _state.update { curState ->
            if (curState.quantityResponse !is ResponseWrapper.Succeed)
                return@update curState


            val newQuantity = try {
                getValidatedQuantity(newValue = newValue)
            } catch (e : IllegalArgumentException){
                curState.quantityResponse.data.pieceQuantity
            }

            curState.copy(
                quantityResponse = ResponseWrapper.Succeed(
                    curState.quantityResponse.data.copy(pieceQuantity = newQuantity)
                ),
                pieceError = null,
            )
        }
    }

    private fun changeUseFromPreviousMonth(){
        _state.update { it.copy(useCalculationFromPreviousMonth = !it.useCalculationFromPreviousMonth) }
        if (_state.value.useCalculationFromPreviousMonth){
            viewModelScope.launch(Dispatchers.IO) {
                getLatestPreviousMonthStock(
                    currentMonthPeriod = period,
                    productId = productId,
                ).map {
                    when(it){
                        is ResponseWrapper.Failed -> ResponseWrapper.Failed()
                        is ResponseWrapper.Loading -> ResponseWrapper.Loading()
                        is ResponseWrapper.Succeed -> ResponseWrapper.Succeed<QuantityField, MyBasicError>(
                            data = QuantityField.from(it.data)
                        )
                    }
                }.collect { response ->
                    _state.update { it.copy(quantityResponse = response) }
                }
            }
        }
    }

    private fun getValidatedQuantity(newValue: String) : Int? {
        if (newValue.length > 6)
            throw IllegalArgumentException()

        val result = newValue.toIntOrNull()
        if ((result == null && newValue.isNotEmpty()) || (result != null && result < 0))
            throw IllegalArgumentException()
        return result
    }

    private fun saveMonthlyStock(){
        val quantityResponse = _state.value.quantityResponse
        if (quantityResponse !is ResponseWrapper.Succeed)
            return

        viewModelScope.launch(Dispatchers.IO) {
            editMonthlyStock(
                cartonQuantity = quantityResponse.data.cartonQuantity,
                pieceQuantity = quantityResponse.data.pieceQuantity,
                monthlyStockEntityId = monthlyStockId,
                monthYearPeriod = period,
                productId = productId,
            ).collect { response ->
                if (response is ResponseWrapper.Failed && response.error != null){
                    _state.update {
                        it.copy(
                            cartonError = response.error.cartonError,
                            pieceError = response.error.pieceError,
                        )
                    }
                }
                _state.update { it.copy(saveMonthlyStockResponse = response) }
            }
        }
    }

    private fun doneHandlingSaveMonthlyStockResponse(){
        _state.update { it.copy(saveMonthlyStockResponse = null) }
    }
}

data class SetMonthlyStockDialogUiState(
    val quantityResponse: ResponseWrapper<QuantityField, MyBasicError>,
    val useCalculationFromPreviousMonth: Boolean = false,
    val saveMonthlyStockResponse: ResponseWrapper<Any?, EditMonthlyStockValidationResult>? = null,
    val cartonError: EditMonthlyStockFieldError? = null,
    val pieceError: EditMonthlyStockFieldError? = null,
)

data class QuantityField(
    val cartonQuantity: Int?,
    val pieceQuantity: Int?,
){
    companion object {
        fun from(value : QuantityPerUnitType) : QuantityField =
            QuantityField(
                cartonQuantity = value.cartonQuantity,
                pieceQuantity = value.pieceQuantity,
            )
    }
}


sealed class EditMonthlyStockDialogEvent {
    class ChangePieceQuantity(val newValue: String) : EditMonthlyStockDialogEvent()
    class ChangeCartonQuantity(val newValue: String): EditMonthlyStockDialogEvent()
    data object ChangeUseFromPreviousMonthStock : EditMonthlyStockDialogEvent()
    data object SaveMonthlyStock : EditMonthlyStockDialogEvent()
    data object DoneHandlingSaveMonthlyStockResponse : EditMonthlyStockDialogEvent()
}