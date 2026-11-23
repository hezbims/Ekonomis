@file:Suppress("FunctionName")

package com.hezapp.ekonomis.test_case.product_preview.presentation

import com.hezapp.ekonomis.app_test_utils.core.application.mock.TestDispatcherProvider
import com.hezapp.ekonomis.app_test_utils.product_preview.application.mock.FakeGetPreviewProductSummariesUseCase
import com.hezapp.ekonomis.base_test_utils.rule.MainDispatcherRule
import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.product_preview.presentation.ProductPreviewEvent
import com.hezapp.ekonomis.product_preview.presentation.ProductPreviewViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductPreviewViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val initialProducts = listOf(
        previewProduct(id = 1, name = "Produk awal"),
    )
    private val oliveOilProducts = listOf(
        previewProduct(id = 2, name = "Minyak Zaitun"),
    )
    private val vegetableOilProducts = listOf(
        previewProduct(id = 3, name = "Minyak Sayur"),
    )

    private lateinit var useCase: FakeGetPreviewProductSummariesUseCase
    private lateinit var viewModel: ProductPreviewViewModel

    @Before
    fun setUp() {
        useCase = FakeGetPreviewProductSummariesUseCase().apply {
            returns(query = "", products = initialProducts)
        }
        viewModel = ProductPreviewViewModel(
            getPreviewProductSummaries = useCase,
            dispatcherProvider = TestDispatcherProvider(mainDispatcherRule.testDispatcher),
        )
    }

    @Test
    fun `When view model is created then products for empty query are loaded`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()

        assertEquals(listOf(""), useCase.requestedQueries)
        assertSucceedWith(initialProducts)
    }

    @Test
    fun `When search query changes then query text updates immediately and request is debounced`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()

        viewModel.onEvent(ProductPreviewEvent.ChangeSearchQuery("minyak zaitun"))
        runCurrent()

        assertEquals("minyak zaitun", viewModel.state.value.searchQuery)
        assertEquals(0, useCase.requestedQueries.count { it == "minyak zaitun" })

        advanceUntilIdle()

        assertEquals(1, useCase.requestedQueries.count { it == "minyak zaitun" })
    }

    @Test
    fun `When user types quickly then only the last query is executed`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()

        listOf("m", "mi", "min", "minyak", "minyak s", "minyak sayur")
            .forEach { query ->
                viewModel.onEvent(ProductPreviewEvent.ChangeSearchQuery(query))
                runCurrent()
            }

        assertEquals(0, useCase.requestedQueries.count { it == "minyak sayur" })
        assertEquals(0, useCase.requestedQueries.count { it == "minyak" })

        advanceUntilIdle()

        assertEquals(1, useCase.requestedQueries.count { it == "minyak sayur" })
        assertEquals(0, useCase.requestedQueries.count { it == "minyak" })
    }

    @Test
    fun `When previous query response arrives after newer query then previous result is ignored`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()
        val oliveOilGate = useCase.gate("minyak zaitun")
        useCase.returns("minyak sayur", vegetableOilProducts)

        viewModel.onEvent(ProductPreviewEvent.ChangeSearchQuery("minyak zaitun"))
        advanceUntilIdle()
        assertEquals(1, useCase.requestedQueries.count { it == "minyak zaitun" })

        viewModel.onEvent(ProductPreviewEvent.ChangeSearchQuery("minyak sayur"))
        advanceUntilIdle()
        assertSucceedWith(vegetableOilProducts)

        oliveOilGate.complete(oliveOilProducts)
        advanceUntilIdle()

        assertEquals("minyak sayur", viewModel.state.value.searchQuery)
        assertSucceedWith(vegetableOilProducts)
        assertTrue(useCase.cancelledQueries.contains("minyak zaitun"))
    }

    @Test
    fun `When state is collected again then products are not reloaded`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()

        val firstCollector = backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.state.collect {}
        }
        runCurrent()
        firstCollector.cancel()

        advanceTimeBy(5_001)
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.state.collect {}
        }
        advanceUntilIdle()

        assertEquals(1, useCase.requestedQueries.count { it == "" })
    }

    @Test
    fun `When retry is requested then current query is loaded immediately`() = runTest(mainDispatcherRule.testDispatcher) {
        advanceUntilIdle()
        useCase.fails("minyak sayur")

        viewModel.onEvent(ProductPreviewEvent.ChangeSearchQuery("minyak sayur"))
        advanceUntilIdle()
        assertTrue(viewModel.state.value.productsResponse is ResponseWrapper.Failed)

        useCase.returns("minyak sayur", vegetableOilProducts)
        viewModel.onEvent(ProductPreviewEvent.LoadProducts)
        runCurrent()

        assertEquals(2, useCase.requestedQueries.count { it == "minyak sayur" })

        advanceUntilIdle()
        assertSucceedWith(vegetableOilProducts)
    }

    @Suppress("UNCHECKED_CAST")
    private fun assertSucceedWith(expected: List<PreviewProductSummary>) {
        val response = viewModel.state.value.productsResponse
        assertTrue("Expected Succeed but was $response", response is ResponseWrapper.Succeed<*, *>)
        assertEquals(
            expected,
            (response as ResponseWrapper.Succeed<List<PreviewProductSummary>, MyBasicError>).data,
        )
    }

    private fun previewProduct(
        id: Int,
        name: String,
    ) = PreviewProductSummary(
        id = id,
        name = name,
        quantity = 1,
        price = 10_000,
        ppn = null,
        unitType = UnitType.PIECE,
    )
}
