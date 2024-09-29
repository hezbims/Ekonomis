package com.hezapp.ekonomis.product_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.product_detail.domain.use_case.GetProductDetailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class ProductDetailViewModel(
    private val productId : Int,
    private val getProductDetail : GetProductDetailUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProductDetailUiState())
    val state = _state.onStart {
        loadDetailProduct()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        ProductDetailUiState(),
    )

    fun onEvent(event : ProductDetailEvent){
        when(event){
            ProductDetailEvent.LoadDetailProduct ->
                loadDetailProduct()
            ProductDetailEvent.ShowChangePeriodDialog ->
                showChangeProductDialog()
            ProductDetailEvent.DismissChangePeriodDialog ->
                dismissChangePeriodDialog()
            is ProductDetailEvent.ChangeCurrentPeriod ->
                changeCurrentPeriod(event.newPeriod)
        }
    }

    private fun loadDetailProduct(){
        viewModelScope.launch(Dispatchers.IO) {
            getProductDetail(
                productId = productId,
                monthYearPeriod = _state.value.currentPeriod,
            ).filter { !it.isLoading() }.collect { response ->
                _state.update { it.copy(detailProductResponse = response) }
            }
        }
    }

    private fun showChangeProductDialog(){
        _state.update { it.copy(showChangePeriodDialog = true) }
    }

    private fun dismissChangePeriodDialog(){
        _state.update { it.copy(showChangePeriodDialog = false) }
    }

    private fun changeCurrentPeriod(newPeriod: Long){
        _state.update { it.copy(currentPeriod = newPeriod, showChangePeriodDialog = false) }
        loadDetailProduct()
    }
}

data class ProductDetailUiState(
    val detailProductResponse: ResponseWrapper<ProductDetail, MyBasicError> = ResponseWrapper.Loading(),
    val currentPeriod: Long = Calendar.getInstance().timeInMillis,
    val showChangePeriodDialog: Boolean = false,
)

sealed class ProductDetailEvent {
    data object LoadDetailProduct : ProductDetailEvent()
    data object ShowChangePeriodDialog : ProductDetailEvent()
    data object DismissChangePeriodDialog : ProductDetailEvent()
    class ChangeCurrentPeriod(val newPeriod: Long) : ProductDetailEvent()
}