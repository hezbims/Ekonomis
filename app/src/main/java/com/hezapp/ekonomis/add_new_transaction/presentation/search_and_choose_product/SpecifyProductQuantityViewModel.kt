package com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product

import com.hezapp.ekonomis.add_new_transaction.domain.use_case.GetValidatedProductPriceUseCase
import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpecifyProductQuantityViewModel(
    product : ProductEntity,
) {
    private val getValidatedProductPrice = GetValidatedProductPriceUseCase()

    private val _state = MutableStateFlow(SpecifyProductQuantityUiState.init(product))
    val state : StateFlow<SpecifyProductQuantityUiState>
        get() = _state.asStateFlow()

    fun onEvent(event : SpecifyProductQuantityEvent){
        when(event){
            is SpecifyProductQuantityEvent.ChangePrice ->
                changePrice(event.newPrice)
            is SpecifyProductQuantityEvent.ChangeUnitType ->
                changeUnitType(event.newUnitType)
            is SpecifyProductQuantityEvent.ChangeQuantity ->
                changeQuantity(event.newQuantity)
            SpecifyProductQuantityEvent.VerifyProductData ->
                verifyProductData()
            SpecifyProductQuantityEvent.DoneHandlingValidData ->
                doneHandlingValidData()
        }
    }

    private fun changePrice(newPrice: String){
        try {
            if (newPrice.isEmpty())
                _state.update { it.copy(price = null, priceHasError = false) }
            else
                _state.update { it.copy(price = getValidatedProductPrice(newPrice), priceHasError = false) }
        } catch (_ : Exception){}
    }

    private fun changeUnitType(unitType: UnitType){
        _state.update { it.copy(unitType = unitType, unitTypeHasError = false) }
    }

    private fun changeQuantity(newQuantity: String){
        try {
            if (newQuantity.isEmpty()){
                _state.update { it.copy(quantity = null, quantityHasError = false) }
                return
            }

            val intNewQuantity = newQuantity.toInt()
            if (intNewQuantity <= 0 || intNewQuantity >= 1000)
                return

            _state.update { it.copy(quantity = intNewQuantity, quantityHasError = false) }
        } catch (_ : NumberFormatException){}
    }

    private fun verifyProductData(){
        val currentState = _state.value
        val unitTypeHasError = currentState.unitType == null
        val quantityHasError = currentState.quantity == null
        val priceHasError = currentState.price == null
        val isDataValid = !unitTypeHasError && !quantityHasError && !priceHasError

        _state.update { it.copy(
            unitTypeHasError = unitTypeHasError,
            quantityHasError = quantityHasError,
            priceHasError = priceHasError,
            isDataValid = isDataValid,
        ) }
    }

    private fun doneHandlingValidData(){
        _state.update { it.copy(isDataValid = false) }
    }
}

data class SpecifyProductQuantityUiState(
    val unitType: UnitType?,
    val unitTypeHasError: Boolean,
    val price: Int?,
    val priceHasError: Boolean,
    val product: ProductEntity,
    val quantity: Int?,
    val quantityHasError: Boolean,
    val isDataValid: Boolean,
){
    companion object {
        fun init(product: ProductEntity) : SpecifyProductQuantityUiState =
            SpecifyProductQuantityUiState(
                unitType = null,
                unitTypeHasError = false,
                price = null,
                priceHasError = false,
                product = product,
                quantity = null,
                quantityHasError = false,
                isDataValid = false,
            )
    }
}

sealed class SpecifyProductQuantityEvent {
    class ChangeUnitType(val newUnitType: UnitType) : SpecifyProductQuantityEvent()
    class ChangePrice(val newPrice : String) : SpecifyProductQuantityEvent()
    class ChangeQuantity(val newQuantity: String) : SpecifyProductQuantityEvent()
    data object VerifyProductData : SpecifyProductQuantityEvent()
    data object DoneHandlingValidData : SpecifyProductQuantityEvent()
}

