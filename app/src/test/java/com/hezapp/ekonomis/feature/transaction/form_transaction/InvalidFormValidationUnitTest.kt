package com.hezapp.ekonomis.feature.transaction.form_transaction

import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionScreen
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.test_application.BaseEkonomisUiUnitTest
import org.junit.Before
import org.junit.Test
import org.koin.core.parameter.parametersOf

class InvalidFormValidationUnitTest : BaseEkonomisUiUnitTest() {
    @Before
    fun prepare(){
        composeRule.setContent {
            AddOrUpdateTransactionScreen(
                navController = rememberNavController(),
                onSubmitSucceed = { },
                onDeleteSucceed = { },
                viewModel = koin.get(parameters = { parametersOf(null) }),
                timeService = koin.get(),
            )
        }

        composeRule.waitForIdle()
    }

    @Test
    fun `When transaction type is not selected, the submit button must not be exist`(){
        utils.transactionFormRobot.assertSubmitButtonNotExist()
    }

    @Test
    fun `When transaction date is not choosen, and user submit the form, there will be error`(){
        utils.transactionFormRobot.apply {
            chooseTransactionType(TransactionType.PEMBELIAN)
            submitTransactionForm()
        }

        utils.transactionFormRobot.assertEmptyDateErrorExist()
    }

    @Test
    fun `When profile is not choosen, and user submit the form, there will be error`(){
        utils.transactionFormRobot.apply {
            chooseTransactionType(TransactionType.PEMBELIAN)
            submitTransactionForm()
        }

        utils.transactionFormRobot.assertEmptyProfileErrorExist()
    }

    @Test
    fun `When transaction type is 'PEMBELIAN', and user don't specify PPN, and user submit the form, there will be error`(){
        utils.transactionFormRobot.apply {
            chooseTransactionType(TransactionType.PEMBELIAN)
            submitTransactionForm()
        }

        utils.transactionFormRobot.assertEmptyPpnErrorExist()
    }

    @Test
    fun `When product list is empty, and user submit the form, there will be error`(){
        utils.transactionFormRobot.apply {
            chooseTransactionType(TransactionType.PEMBELIAN)
            submitTransactionForm()
        }

        utils.transactionFormRobot.assertEmptyProductListErrorExist()
    }
}