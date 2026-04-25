package com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.edit_product_name

import com.hezapp.ekonomis._testing_only.test_dao.ProductTestDao
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.EditProductNameError
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.EditProductNameUseCase
import com.hezapp.ekonomis.test_application.BaseDataUnitTest
import com.hezapp.ekonomis.test_utils.seeder.ProductSeeder
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before

@Suppress("ClassName")
abstract class _BaseEditProductNameUnitTest : BaseDataUnitTest() {

    protected lateinit var editProductName: EditProductNameUseCase
    private lateinit var productSeeder: ProductSeeder
    private lateinit var productTestDao: ProductTestDao

    private var lastResponse: ResponseWrapper<Any?, EditProductNameError>? = null

    @Before
    fun prepareBase() {
        editProductName = koin.get<EditProductNameUseCase>()
        productSeeder = dataUtils.productSeeder
        productTestDao = koin.get<ProductTestDao>()
    }

    protected fun userHasProductWithNames(vararg names: String) = runTest {
        productSeeder.run(*names)
    }

    protected fun userEditProduct(withName: String, toName: String) = runTest {
        val target = productTestDao.getByExactNames(withName).single()

        lastResponse = editProductName(productId = target.id, name = toName).last()
    }

    protected fun userEditProduct(withId: Int, toName: String) = runTest {
        lastResponse = editProductName(productId = withId, name = toName).last()
    }

    protected fun productsCountInDatabaseShouldBe(count: Int) = runTest {
        val actual = productTestDao.count()
        assertEquals("Expected $count products in database but found $actual", count, actual)
    }

    protected fun databaseShouldContainsProductWithNames(vararg names: String) = runTest {
        val foundProductNames = productTestDao.getAll().map {
            it.name
        }
        val foundProductNamesJoined = foundProductNames.joinToString(", ")

        names.forEach { expectedName ->
            if (!foundProductNames.contains(expectedName))
                error("Expected product '$expectedName' to exist in database, but found: $foundProductNamesJoined")
        }
    }

    protected fun useCaseResultShouldBeSucceed() {
        val response = lastResponse
        assertTrue(
            "Expected last response to be Succeed but was $response",
            response?.isSucceed() == true,
        )
    }

    protected fun useCaseResultShouldFailWith(expectedError: EditProductNameError) {
        val response = lastResponse
        assertTrue(
            "Expected last response to be Failed but was $response",
            response?.isFailed() == true,
        )
        assertEquals(
            "Expected error $expectedError but got ${(response as? ResponseWrapper.Failed)?.error}",
            expectedError,
            (response as ResponseWrapper.Failed).error,
        )
    }
}

