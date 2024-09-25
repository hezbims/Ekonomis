package com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.GetAllProductsUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.InsertNewProductUseCase
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.model.InsertProductError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchAndChooseProductViewModel(
    private val getAllProducts: GetAllProductsUseCase,
    private val insertNewProduct: InsertNewProductUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchAndChooseProductUiState.init())
    val state : StateFlow<SearchAndChooseProductUiState>
        get() = _state.asStateFlow()

    init { loadAvailableProducts() }

    fun onEvent(event : SearchAndChooseProductEvent){
        when (event){
            is SearchAndChooseProductEvent.ChangeSearchQuery ->
                changeSearchQuery(event.newSearchQuery)
            SearchAndChooseProductEvent.LoadAvailableProducts ->
                loadAvailableProducts()
            SearchAndChooseProductEvent.DoneHandlingRegisterProductResponse ->
                doneHandlingRegisterProductResponse()
            is SearchAndChooseProductEvent.RegisterNewProduct ->
                registerNewProduct(event.productName)
            is SearchAndChooseProductEvent.SelectProductForSpecification ->
                selectProductForSpecification(event.product)
            SearchAndChooseProductEvent.DoneSelectProductSpecification ->
                doneSelectProductSpecification()
        }
    }

    private fun changeSearchQuery(searchQuery: String){
        _state.update { it.copy(searchQuery = searchQuery) }
        loadAvailableProducts()
    }

    private fun loadAvailableProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllProducts(searchQuery = _state.value.searchQuery).collect { response ->
                _state.update { it.copy(availableProductsResponse = response) }
            }
        }
    }

    private fun registerNewProduct(productName : String){
        viewModelScope.launch(Dispatchers.IO) {
            insertNewProduct(ProductEntity(id = 0 , name = productName)).collect { response ->
                _state.update { it.copy(registerNewProductResponse = response) }
                if (response is ResponseWrapper.Succeed)
                    loadAvailableProducts()
            }
        }
    }

    private fun doneHandlingRegisterProductResponse(){
        _state.update { it.copy(registerNewProductResponse = null) }
    }

    private fun selectProductForSpecification(product: ProductEntity){
        _state.update { it.copy(currentChoosenProduct = product) }
    }

    private fun doneSelectProductSpecification(){
        _state.update { it.copy(currentChoosenProduct = null) }
    }
}

data class SearchAndChooseProductUiState(
    val searchQuery: String,
    val availableProductsResponse : ResponseWrapper<List<ProductEntity> , MyBasicError>,
    val registerNewProductResponse : ResponseWrapper<Any?, InsertProductError>?,
    val currentChoosenProduct : ProductEntity?,
){
    companion object {
        fun init() : SearchAndChooseProductUiState =
            SearchAndChooseProductUiState(
                searchQuery = "",
                availableProductsResponse = ResponseWrapper.Loading(),
                registerNewProductResponse = null,
                currentChoosenProduct = null,
            )
    }
}

sealed class SearchAndChooseProductEvent {
    class ChangeSearchQuery(val newSearchQuery: String) : SearchAndChooseProductEvent()
    data object LoadAvailableProducts : SearchAndChooseProductEvent()
    class RegisterNewProduct(val productName: String) : SearchAndChooseProductEvent()
    data object DoneHandlingRegisterProductResponse : SearchAndChooseProductEvent()
    class SelectProductForSpecification(val product: ProductEntity) : SearchAndChooseProductEvent()
    data object DoneSelectProductSpecification : SearchAndChooseProductEvent()

}