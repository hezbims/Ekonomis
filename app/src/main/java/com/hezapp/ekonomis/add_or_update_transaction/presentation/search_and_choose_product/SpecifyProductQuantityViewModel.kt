package com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product

import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.input_form_manipulation.GetValidatedProductPriceUseCase
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.InvoiceItemUiModel
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpecifyProductQuantityViewModel private constructor(
    initialUiState: SpecifyProductQuantityUiState
) {
    constructor(product: ProductEntity) : this(
        SpecifyProductQuantityUiState(
            id = 0,
            unitType = null,
            listId = null,
            price = null,
            quantity = null,
            product = product,
        )
    )

    constructor(invoiceItem: InvoiceItemUiModel) : this(
        SpecifyProductQuantityUiState(
            id = invoiceItem.id,
            unitType = invoiceItem.unitType,
            listId = invoiceItem.listId,
            price = invoiceItem.price,
            product = ProductEntity(id = invoiceItem.productId, name = invoiceItem.productName),
            quantity = invoiceItem.quantity,
        )
    )

    private val getValidatedProductPrice = GetValidatedProductPriceUseCase()

    private val _state = MutableStateFlow(initialUiState)
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
    val unitTypeHasError: Boolean = false,
    val price: Int?,
    val priceHasError: Boolean = false,
    val product: ProductEntity,
    val listId: String?,
    val quantity: Int?,
    val quantityHasError: Boolean = false,
    val isDataValid: Boolean = false,
    val id: Int,
)

sealed class SpecifyProductQuantityEvent {
    class ChangeUnitType(val newUnitType: UnitType) : SpecifyProductQuantityEvent()
    class ChangePrice(val newPrice : String) : SpecifyProductQuantityEvent()
    class ChangeQuantity(val newQuantity: String) : SpecifyProductQuantityEvent()
    data object VerifyProductData : SpecifyProductQuantityEvent()
    data object DoneHandlingValidData : SpecifyProductQuantityEvent()
}

