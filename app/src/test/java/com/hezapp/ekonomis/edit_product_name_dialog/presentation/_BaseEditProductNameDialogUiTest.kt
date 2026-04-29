package com.hezapp.ekonomis.edit_product_name_dialog.presentation

import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.EditProductNameError
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.iface.IEditProductNameUseCase
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertTrue
import org.junit.Before
import org.koin.compose.KoinIsolatedContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@Suppress("ClassName")
abstract class _BaseEditProductNameDialogUiTest : BaseEkonomisUiUnitTest(
    loadDefaultKoinModules = false
) {
    private var isDialogClosed = false

    @Before
    fun setupKoinModules() {
        koin.loadModules(
            listOf(
                module {
                    factory<IEditProductNameUseCase> { FakeEditProductNameUseCase() }
                    viewModel { params ->
                        EditProductNameDialogViewModel(
                            productId = params.get(),
                            editProductName = get(),
                        )
                    }
                }
            ),
            allowOverride = true,
        )
    }

    fun userOpennedEditProductNameDialog() {
        isDialogClosed = false
        composeRule.setContent {
            KoinIsolatedContext(koinApp) {
                EditProductNameDialog(
                    productId = 1,
                    onEdited = { isDialogClosed = true },
                    onDismissRequest = { isDialogClosed = true },
                )
            }
        }
    }

    fun editProductName(to: String) {
        uiUtils.editProductNameDialogRobot.enterName(to)
        uiUtils.editProductNameDialogRobot.clickSave()
    }

    fun theDialogShouldDisplayAppropriateErrorMessage() {
        uiUtils.editProductNameDialogRobot.assertErrorMessageDisplayed(
            appContext.getString(R.string.name_already_used))

    }

    fun theDialogShouldBeDismissed() {
        composeRule.waitForIdle()
        assertTrue("Expected dialog to be closed but it wasn't", isDialogClosed)
    }
}

private class FakeEditProductNameUseCase : IEditProductNameUseCase {
    override fun invoke(
        productId: Int,
        name: String,
    ): Flow<ResponseWrapper<Any?, EditProductNameError>> = flow {
        emit(ResponseWrapper.Loading())
        if (name == "already-exist-product-name") {
            emit(ResponseWrapper.Failed(EditProductNameError.ProductNameAlreadyExist))
        } else {
            emit(ResponseWrapper.Succeed(null))
        }
    }
}