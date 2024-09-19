package com.hezapp.ekonomis.product_preview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.product_preview.domain.use_case.GetPreviewProductSummariesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductPreviewViewModel : ViewModel() {
    private val getPreviewProductSummaries = GetPreviewProductSummariesUseCase()

    private val _state = MutableStateFlow(ProductPreviewUiState())
    val state : StateFlow<ProductPreviewUiState>
        get() = _state.asStateFlow()

    fun onEvent(event: ProductPreviewEvent){
        when(event){
            ProductPreviewEvent.LoadProducts ->
                loadProducts()
        }
    }

    private fun loadProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            getPreviewProductSummaries().collect { response ->
                _state.update {
                    it.copy(productsResponse = response)
                }
            }
        }
    }
}

data class ProductPreviewUiState(
    val productsResponse : ResponseWrapper<List<PreviewProductSummary>, MyBasicError> = ResponseWrapper.Loading(),
)

sealed class ProductPreviewEvent {
    data object LoadProducts : ProductPreviewEvent()
}