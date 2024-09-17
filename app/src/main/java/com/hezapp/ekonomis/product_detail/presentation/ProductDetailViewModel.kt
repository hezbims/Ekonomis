package com.hezapp.ekonomis.product_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.product_detail.domain.use_case.GetProductDetailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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
            is ProductDetailEvent.ClickTransactionItem ->
                clickTransactionItem(
                    item = event.item,
                    isOutTransaction = event.isOutTransaction,
                )
        }
    }

    private fun loadDetailProduct(){
        viewModelScope.launch(Dispatchers.IO) {
            getProductDetail(productId).map {
                when(it){
                    is ResponseWrapper.Failed -> ResponseWrapper.Failed<ProductDetailUiModel, MyBasicError>()
                    is ResponseWrapper.Loading -> ResponseWrapper.Loading()
                    is ResponseWrapper.Succeed -> ResponseWrapper.Succeed(data = it.data.toUiModel())
                }
            }.collect { response ->
                _state.update { it.copy(detailProductResponse = response) }
            }
        }
    }

    private fun clickTransactionItem(
        item: ProductTransactionUiModel,
        isOutTransaction: Boolean,
    ){
        _state.update {
            val productDetail = it.detailProductResponse.asSucceed().data
            var updatedList =
                if (isOutTransaction)
                    productDetail.outProductTransactions
                else
                    productDetail.inProductTransactions

            updatedList = updatedList.map { transactionItem ->
                if (transactionItem.data.id == item.data.id)
                    transactionItem.copy(
                        isExpanded = !transactionItem.isExpanded
                    )
                else
                    transactionItem
            }

            it.copy(
                detailProductResponse = ResponseWrapper.Succeed(
                    data = productDetail.let { curProductDetail ->
                        if (isOutTransaction)
                            curProductDetail.copy(outProductTransactions = updatedList)
                        else
                            curProductDetail.copy(inProductTransactions = updatedList)
                    }
                )
            )
        }
    }
}

data class ProductDetailUiState(
    val detailProductResponse: ResponseWrapper<ProductDetailUiModel, MyBasicError> = ResponseWrapper.Loading(),
)

sealed class ProductDetailEvent {
    data object LoadDetailProduct : ProductDetailEvent()
    class ClickTransactionItem(
        val item : ProductTransactionUiModel,
        val isOutTransaction : Boolean,
    ) : ProductDetailEvent()
}