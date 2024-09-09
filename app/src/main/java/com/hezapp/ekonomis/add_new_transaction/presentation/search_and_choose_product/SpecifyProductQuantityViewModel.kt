package com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product

import com.hezapp.ekonomis.add_new_transaction.domain.use_case.GetValidatedProductPriceUseCase
import com.hezapp.ekonomis.core.data.repo.FakeProductRepo
import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType
import com.hezapp.ekonomis.core.domain.product.IProductRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpecifyProductQuantityViewModel(
    product : ProductEntity,
) {
    private val repo : IProductRepo = FakeProductRepo()
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
        }
    }

    private fun changePrice(newPrice: String){
        try {
            if (newPrice.isEmpty())
                _state.update { it.copy(price = null) }
            else
                _state.update { it.copy(price = getValidatedProductPrice(newPrice)) }
        } catch (_ : Exception){}
    }

    private fun changeUnitType(unitType: UnitType){
        _state.update { it.copy(unitType = unitType) }
    }

    private fun changeQuantity(newQuantity: String){
        try {
            if (newQuantity.isEmpty()){
                _state.update { it.copy(quantity = null) }
                return
            }

            val intNewQuantity = newQuantity.toInt()
            if (intNewQuantity <= 0 || intNewQuantity >= 1000)
                return

            _state.update { it.copy(quantity = intNewQuantity) }
        } catch (_ : NumberFormatException){}
    }
}

data class SpecifyProductQuantityUiState(
    val unitType: UnitType?,
    val unitTypeError: String?,
    val price: Int?,
    val priceError: String?,
    val product: ProductEntity,
    val quantity: Int?,
    val quantityError: String?,
){
    companion object {
        fun init(product: ProductEntity) : SpecifyProductQuantityUiState =
            SpecifyProductQuantityUiState(
                unitType = null,
                unitTypeError = null,
                price = null,
                priceError = null,
                product = product,
                quantity = null,
                quantityError = null,
            )
    }
}

sealed class SpecifyProductQuantityEvent {
    class ChangeUnitType(val newUnitType: UnitType) : SpecifyProductQuantityEvent()
    class ChangePrice(val newPrice : String) : SpecifyProductQuantityEvent()
    class ChangeQuantity(val newQuantity: String) : SpecifyProductQuantityEvent()
}

