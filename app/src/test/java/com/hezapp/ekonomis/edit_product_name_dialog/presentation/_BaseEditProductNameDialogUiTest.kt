package com.hezapp.ekonomis.edit_product_name_dialog.presentation

import androidx.activity.compose.setContent
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.EditProductNameError
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.GetProductByIdError
import com.hezapp.ekonomis.edit_product_name_dialog.application.model.ProductByIdPreviewDto
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.iface.IEditProductNameUseCase
import com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.iface.IGetProductByIdUseCase
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import com.hezapp.ekonomis.test_utils.FakeErrorReportingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.koin.compose.KoinIsolatedContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@Suppress("ClassName")
abstract class _BaseEditProductNameDialogUiTest : BaseEkonomisUiUnitTest(
    loadDefaultKoinModules = false
) {
    private var isDialogDismissed = false
    private var isEditSucceed = false

    @Before
    fun setupKoinModules() {
        koin.loadModules(
            listOf(
                module {
                    factory<IEditProductNameUseCase> { FakeEditProductNameUseCase() }
                    factory<IGetProductByIdUseCase> { FakeGetProductByIdUseCase() }
                    factory<IErrorReportingService> { FakeErrorReportingService() }
                    viewModel { _ ->
                        EditProductNameDialogViewModel(
                            productId = 1,
                            getProductById = get(),
                            editProductName = get(),
                            reportingService = get(),
                        )
                    }
                }
            ),
            allowOverride = true,
        )
    }

    fun userOpennedEditProductNameDialog() {
        isDialogDismissed = false
        composeRule.activity.setContent {
            KoinIsolatedContext(koinApp) {
                EditProductNameDialog(
                    productId = 1,
                    onEdited = {
                        isEditSucceed = true
                    },
                    onDismissRequest = {
                        isDialogDismissed = true
                    },
                )
            }
        }
    }

    fun editProductName(to: String) {
        uiUtils.editProductNameDialogRobot.replaceName(to)
        uiUtils.editProductNameDialogRobot.clickSave()
    }

    fun userClickCancelButton(){
        uiUtils.editProductNameDialogRobot.clickCancelButton()
    }

    fun theDialogShouldDisplayAppropriateErrorMessage() {
        uiUtils.editProductNameDialogRobot.assertErrorMessageDisplayed(
            appContext.getString(R.string.name_already_used))

    }

    fun theDialogShouldBeDismissed() {
        composeRule.waitUntil(
            conditionDescription = "Expected dialog to be dismissed but it wasn't",
            timeoutMillis = 2_500L
        ) {
            isDialogDismissed
        }
    }

    fun onEditedCallbackShouldBeCalled(){
        composeRule.waitUntil(
            conditionDescription = "Expected onEditedCallback should be called",
            timeoutMillis = 2_500L
        ) {
            isEditSucceed
        }
    }

    fun theDialogShouldDisplayInitialEditedTargetProductName(){
        uiUtils.editProductNameDialogRobot.assertTextFieldContent("fake-loaded-product-name")
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

private class FakeGetProductByIdUseCase : IGetProductByIdUseCase {
    override fun invoke(id: Int): Flow<ResponseWrapper<ProductByIdPreviewDto, GetProductByIdError>> = flow {
        emit(ResponseWrapper.Loading())
        emit(ResponseWrapper.Succeed(data = ProductByIdPreviewDto(name = "fake-loaded-product-name")))
    }
}