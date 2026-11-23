package com.hezapp.ekonomis.product_preview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.core.application.utils.IDispatcherProvider
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.product_preview.application.use_case.iface.IGetPreviewProductSummariesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductPreviewViewModel(
    private val getPreviewProductSummaries : IGetPreviewProductSummariesUseCase,
    private val dispatcherProvider: IDispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductPreviewUiState())
    val state: StateFlow<ProductPreviewUiState> = _state.asStateFlow()

    private var requestGeneration = 0L
    private val searchRequest = MutableStateFlow(
        SearchRequest(
            query = "",
            generation = requestGeneration,
            debounce = false,
        )
    )

    init {
        observeProductSummaries()
    }

    fun onEvent(event: ProductPreviewEvent){
        when(event){
            ProductPreviewEvent.LoadProducts ->
                reloadProducts()
            is ProductPreviewEvent.ChangeSearchQuery ->
                changeSearchQuery(event.newSearchQuery)
        }
    }

    /**
     * Query terbaru selalu menggantikan query sebelumnya. Generation dipakai untuk
     * menolak response lama yang datang terlambat.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeProductSummaries() {
        viewModelScope.launch(dispatcherProvider.io) {
            searchRequest
                .flatMapLatest { request ->
                    if (request.debounce)
                        delay(SEARCH_DEBOUNCE_MS)

                    getPreviewProductSummaries(
                        searchQuery = request.query,
                    ).map { response -> request to response }
                }
                .collect { (request, response) ->
                    if (request.generation == searchRequest.value.generation &&
                        request.query == _state.value.searchQuery
                    ) {
                        _state.update {
                            it.copy(productsResponse = response)
                        }
                    }
                }
        }
    }

    private fun changeSearchQuery(newSearchQuery: String){
        if (newSearchQuery == _state.value.searchQuery)
            return

        _state.update { it.copy(searchQuery = newSearchQuery) }
        requestProducts(
            query = newSearchQuery,
            debounce = true,
        )
    }

    private fun reloadProducts() {
        requestProducts(
            query = _state.value.searchQuery,
            debounce = false,
        )
    }

    private fun requestProducts(
        query: String,
        debounce: Boolean,
    ) {
        requestGeneration++
        searchRequest.value = SearchRequest(
            query = query,
            generation = requestGeneration,
            debounce = debounce,
        )
    }

    private data class SearchRequest(
        val query: String,
        val generation: Long,
        val debounce: Boolean,
    )

    companion object {
        internal const val SEARCH_DEBOUNCE_MS = 300L
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