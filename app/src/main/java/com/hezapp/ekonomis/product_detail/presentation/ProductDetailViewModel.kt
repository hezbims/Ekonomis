package com.hezapp.ekonomis.product_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.product_detail.domain.use_case.GetProductDetailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(private val productId : Int) : ViewModel() {
    private val getProductDetail = GetProductDetailUseCase()

    private val _state = MutableStateFlow(ProductDetailUiState())
    val state : StateFlow<ProductDetailUiState>
        get() = _state.asStateFlow()

    fun onEvent(event : ProductDetailEvent){
        when(event){
            ProductDetailEvent.LoadDetailProduct ->
                loadDetailProduct()
        }
    }

    private fun loadDetailProduct(){
        viewModelScope.launch(Dispatchers.IO) {
            getProductDetail(productId).collect { response ->
                _state.update { it.copy(detailProductResponse = response) }
            }
        }
    }
}

data class ProductDetailUiState(
    val detailProductResponse: ResponseWrapper<ProductDetail, MyBasicError> = ResponseWrapper.Loading(),
)

sealed class ProductDetailEvent {
    data object LoadDetailProduct : ProductDetailEvent()
}