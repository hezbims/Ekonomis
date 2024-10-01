package com.hezapp.ekonomis.product_preview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.product_preview.domain.use_case.GetPreviewProductSummariesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductPreviewViewModel(
    private val getPreviewProductSummaries : GetPreviewProductSummariesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductPreviewUiState())
    val state = _state.onStart {
        loadProducts()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        ProductPreviewUiState(),
    )

    fun onEvent(event: ProductPreviewEvent){
        when(event){
            ProductPreviewEvent.LoadProducts ->
                loadProducts()
            is ProductPreviewEvent.ChangeSearchQuery ->
                changeSearchQuery(event.newSearchQuery)
        }
    }

    private fun loadProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            getPreviewProductSummaries(
                searchQuery = _state.value.searchQuery,
            ).collect { response ->
                _state.update {
                    it.copy(productsResponse = response)
                }
            }
        }
    }

    private fun changeSearchQuery(newSearchQuery: String){
        _state.update { it.copy(searchQuery = newSearchQuery) }
        loadProducts()
    }
}

data class ProductPreviewUiState(
    val productsResponse : ResponseWrapper<List<PreviewProductSummary>, MyBasicError> = ResponseWrapper.Loading(),
    val searchQuery: String = "",
)

sealed class ProductPreviewEvent {
    data object LoadProducts : ProductPreviewEvent()
    class ChangeSearchQuery(val newSearchQuery: String) : ProductPreviewEvent()
}