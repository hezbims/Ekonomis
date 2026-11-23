package com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.GetAllProductsUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.InsertNewProductUseCase
import com.hezapp.ekonomis.core.application.utils.IDispatcherProvider
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.product.model.InsertProductError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchAndChooseProductViewModel(
    private val getAllProducts: GetAllProductsUseCase,
    private val insertNewProduct: InsertNewProductUseCase,
    private val dispatcherProvider: IDispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchAndChooseProductUiState.init())
    val state : StateFlow<SearchAndChooseProductUiState>
        get() = _state.asStateFlow()

    private var requestGeneration = 0L
    private val searchRequest = MutableStateFlow(
        SearchRequest(
            query = "",
            generation = requestGeneration,
            debounce = false,
        )
    )

    init {
        observeAvailableProducts()
    }

    fun onEvent(event : SearchAndChooseProductEvent){
        when (event){
            is SearchAndChooseProductEvent.ChangeSearchQuery ->
                changeSearchQuery(event.newSearchQuery)
            SearchAndChooseProductEvent.LoadAvailableProducts ->
                reloadAvailableProducts()
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

    /**
     * Query terbaru selalu menggantikan query sebelumnya. Generation dipakai untuk
     * menolak response lama yang datang terlambat.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAvailableProducts() {
        viewModelScope.launch(dispatcherProvider.io) {
            searchRequest
                .flatMapLatest { request ->
                    if (request.debounce)
                        delay(SEARCH_DEBOUNCE_MS)

                    getAllProducts(searchQuery = request.query)
                        .map { response -> request to response }
                }
                .collect { (request, response) ->
                    if (request.generation == searchRequest.value.generation &&
                        request.query == _state.value.searchQuery
                    ) {
                        _state.update {
                            it.copy(availableProductsResponse = response)
                        }
                    }
                }
        }
    }

    private fun changeSearchQuery(searchQuery: String){
        if (searchQuery == _state.value.searchQuery)
            return

        _state.update { it.copy(searchQuery = searchQuery) }
        requestAvailableProducts(
            query = searchQuery,
            debounce = true,
        )
    }

    private fun reloadAvailableProducts(){
        requestAvailableProducts(
            query = _state.value.searchQuery,
            debounce = false,
        )
    }

    private fun requestAvailableProducts(
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

    private fun registerNewProduct(productName : String){
        viewModelScope.launch {
            insertNewProduct(ProductEntity(id = 0 , name = productName)).collect { response ->
                _state.update { it.copy(registerNewProductResponse = response) }
                if (response is ResponseWrapper.Succeed)
                    reloadAvailableProducts()
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

    private data class SearchRequest(
        val query: String,
        val generation: Long,
        val debounce: Boolean,
    )

    companion object {
        internal const val SEARCH_DEBOUNCE_MS = 300L
    }
}

/**
 * @property registerNewProductResponse Nunjukin response apakah user berhasil menambahkan nama produk baru. Kalau sukses
 * maka [availableProductsResponse] bakal di refresh.
 *
 * @property currentChoosenProduct Produk yang sekarang sedang dipencet atau dipilih sama user untuk masuk ke bottom sheet.
 * Kalau ini bukan null, maka bottom sheet kebuka
 */
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
