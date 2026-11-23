@file:Suppress("FunctionName")

package com.hezapp.ekonomis.test_case.search_and_choose_product.presentation

import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.GetAllProductsUseCase
import com.hezapp.ekonomis.add_or_update_transaction.domain.use_case.product.InsertNewProductUseCase
import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product.SearchAndChooseProductEvent
import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product.SearchAndChooseProductViewModel
import com.hezapp.ekonomis.app_test_utils.core.application.mock.TestDispatcherProvider
import com.hezapp.ekonomis.base_test_utils.rule.MainDispatcherRule
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SearchAndChooseProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val initialProducts = listOf(
        ProductEntity(id = 1, name = "Produk awal"),
    )
    private val oliveOilProducts = listOf(
        ProductEntity(id = 2, name = "Minyak Zaitun"),
    )
    private val vegetableOilProducts = listOf(
        ProductEntity(id = 3, name = "Minyak Sayur"),
    )
    private val updatedVegetableOilProducts = listOf(
        ProductEntity(id = 3, name = "Minyak Sayur"),
        ProductEntity(id = 4, name = "Minyak Sayur Baru"),
    )

    private val requestedQueries = mutableListOf<String>()
    private val cancelledQueries = mutableListOf<String>()
    private val outcomes = mutableMapOf<String, ArrayDeque<Outcome>>()

    private lateinit var getAllProducts: GetAllProductsUseCase
    private lateinit var insertNewProduct: InsertNewProductUseCase
    private lateinit var viewModel: SearchAndChooseProductViewModel

    @Before
    fun setUp() {
        returns("", initialProducts)

        getAllProducts = mock()
        whenever(getAllProducts(any())).thenAnswer { invocation ->
            val query = invocation.getArgument<String>(0)
            requestedQueries += query
            productsFlow(query)
        }

        insertNewProduct = mock()
        whenever(insertNewProduct(any())).thenReturn(
            flowOf(
                ResponseWrapper.Loading(),
                ResponseWrapper.Succeed(null),
            )
        )

        viewModel = SearchAndChooseProductViewModel(
            getAllProducts = getAllProducts,
            insertNewProduct = insertNewProduct,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        )
    }

    @Test
    fun `When view model is created then products for empty query are loaded`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()

        assertEquals(listOf(""), requestedQueries)
        assertSucceedWith(initialProducts)
    }

    @Test
    fun `When search query changes then query text updates immediately and request is debounced`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()

        viewModel.onEvent(SearchAndChooseProductEvent.ChangeSearchQuery("minyak zaitun"))
        runCurrent()

        assertEquals("minyak zaitun", viewModel.state.value.searchQuery)
        assertEquals(0, requestedQueries.count { it == "minyak zaitun" })

        advanceUntilIdle()

        assertEquals(1, requestedQueries.count { it == "minyak zaitun" })
    }

    @Test
    fun `When user types quickly then only the last query is executed`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()

        listOf("m", "mi", "min", "minyak", "minyak s", "minyak sayur")
            .forEach { query ->
                viewModel.onEvent(SearchAndChooseProductEvent.ChangeSearchQuery(query))
                runCurrent()
            }

        assertEquals(0, requestedQueries.count { it == "minyak sayur" })
        assertEquals(0, requestedQueries.count { it == "minyak" })

        advanceUntilIdle()

        assertEquals(1, requestedQueries.count { it == "minyak sayur" })
        assertEquals(0, requestedQueries.count { it == "minyak" })
    }

    @Test
    fun `When previous query response arrives after newer query then previous result is ignored`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()
        val oliveOilGate = gate("minyak zaitun")
        returns("minyak sayur", vegetableOilProducts)

        viewModel.onEvent(SearchAndChooseProductEvent.ChangeSearchQuery("minyak zaitun"))
        advanceUntilIdle()
        assertEquals(1, requestedQueries.count { it == "minyak zaitun" })

        viewModel.onEvent(SearchAndChooseProductEvent.ChangeSearchQuery("minyak sayur"))
        advanceUntilIdle()
        assertSucceedWith(vegetableOilProducts)

        oliveOilGate.complete(oliveOilProducts)
        advanceUntilIdle()

        assertEquals("minyak sayur", viewModel.state.value.searchQuery)
        assertSucceedWith(vegetableOilProducts)
        assertTrue(cancelledQueries.contains("minyak zaitun"))
    }

    @Test
    fun `When retry is requested then current query is loaded immediately`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()
        fails("minyak sayur")

        viewModel.onEvent(SearchAndChooseProductEvent.ChangeSearchQuery("minyak sayur"))
        advanceUntilIdle()
        assertTrue(viewModel.state.value.availableProductsResponse is ResponseWrapper.Failed)

        returns("minyak sayur", vegetableOilProducts)
        viewModel.onEvent(SearchAndChooseProductEvent.LoadAvailableProducts)
        runCurrent()

        assertEquals(2, requestedQueries.count { it == "minyak sayur" })

        advanceUntilIdle()
        assertSucceedWith(vegetableOilProducts)
    }

    @Test
    fun `When new product is registered successfully then current query is reloaded`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()
        returns("minyak sayur", vegetableOilProducts)
        returns("minyak sayur", updatedVegetableOilProducts)

        viewModel.onEvent(SearchAndChooseProductEvent.ChangeSearchQuery("minyak sayur"))
        advanceUntilIdle()
        assertSucceedWith(vegetableOilProducts)

        viewModel.onEvent(SearchAndChooseProductEvent.RegisterNewProduct("Minyak Sayur Baru"))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.registerNewProductResponse is ResponseWrapper.Succeed<*, *>)
        assertEquals(2, requestedQueries.count { it == "minyak sayur" })
        assertSucceedWith(updatedVegetableOilProducts)
    }

    @Suppress("UNCHECKED_CAST")
    private fun assertSucceedWith(expected: List<ProductEntity>) {
        val response = viewModel.state.value.availableProductsResponse
        assertTrue("Expected Succeed but was $response", response is ResponseWrapper.Succeed<*, *>)
        assertEquals(
            expected,
            (response as ResponseWrapper.Succeed<List<ProductEntity>, MyBasicError>).data,
        )
    }

    private fun productsFlow(query: String): Flow<ResponseWrapper<List<ProductEntity>, MyBasicError>> =
        flow {
            emit(ResponseWrapper.Loading())

            try {
                when (val outcome = outcomes[query]?.removeFirstOrNull()) {
                    is Outcome.Success -> emit(ResponseWrapper.Succeed(outcome.products))
                    is Outcome.Gated -> emit(ResponseWrapper.Succeed(outcome.gate.await()))
                    Outcome.Failure, null -> emit(ResponseWrapper.Failed())
                }
            } finally {
                if (!currentCoroutineContext().isActive)
                    cancelledQueries += query
            }
        }

    private fun returns(
        query: String,
        products: List<ProductEntity>,
    ) {
        enqueue(query, Outcome.Success(products))
    }

    private fun fails(query: String) {
        enqueue(query, Outcome.Failure)
    }

    private fun gate(query: String): CompletableDeferred<List<ProductEntity>> {
        val gate = CompletableDeferred<List<ProductEntity>>()
        enqueue(query, Outcome.Gated(gate))
        return gate
    }

    private fun enqueue(query: String, outcome: Outcome) {
        outcomes.getOrPut(query) { ArrayDeque() }.addLast(outcome)
    }

    private sealed interface Outcome {
        data class Success(val products: List<ProductEntity>) : Outcome
        data class Gated(val gate: CompletableDeferred<List<ProductEntity>>) : Outcome
        data object Failure : Outcome
    }
}
