package com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.data.repo.FakeProductRepo
import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.IProductRepo
import com.hezapp.ekonomis.core.domain.product.InsertProductError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchAndChooseProductViewModel : ViewModel() {
    private val productRepo : IProductRepo = FakeProductRepo()

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
        }
    }

    private fun changeSearchQuery(searchQuery: String){
        _state.update { it.copy(searchQuery = searchQuery) }
        loadAvailableProducts()
    }

    private fun loadAvailableProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            productRepo.getAllProduct(searchQuery = _state.value.searchQuery).collect { response ->
                _state.update { it.copy(availableProductsResponse = response) }
            }
        }
    }

    private fun registerNewProduct(productName : String){
        viewModelScope.launch(Dispatchers.IO) {
            productRepo.insertProduct(ProductEntity(name = productName)).collect { response ->
                _state.update { it.copy(registerNewProductResponse = response) }
                if (response is ResponseWrapper.Succeed)
                    loadAvailableProducts()
            }
        }
    }

    private fun doneHandlingRegisterProductResponse(){
        _state.update { it.copy(registerNewProductResponse = null) }
    }
}

data class SearchAndChooseProductUiState(
    val searchQuery: String,
    val availableProductsResponse : ResponseWrapper<List<ProductEntity> , MyBasicError>,
    val registerNewProductResponse : ResponseWrapper<Any?, InsertProductError>?,
){
    companion object {
        fun init() : SearchAndChooseProductUiState =
            SearchAndChooseProductUiState(
                searchQuery = "",
                availableProductsResponse = ResponseWrapper.Loading(),
                registerNewProductResponse = null,
            )
    }
}

sealed class SearchAndChooseProductEvent {
    class ChangeSearchQuery(val newSearchQuery: String) : SearchAndChooseProductEvent()
    data object LoadAvailableProducts : SearchAndChooseProductEvent()
    class RegisterNewProduct(val productName: String) : SearchAndChooseProductEvent()
    data object DoneHandlingRegisterProductResponse : SearchAndChooseProductEvent()

}