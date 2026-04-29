package com.hezapp.ekonomis.test_utils

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.robot.EditProductNameDialogRobot
import com.hezapp.ekonomis.robot.ProductDetailRobot
import com.hezapp.ekonomis.robot.ProductPreviewRobot
import com.hezapp.ekonomis.robot.transaction_form.TransactionFormRobot
import com.hezapp.ekonomis.robot.transaction_history.TransactionHistoryRobot
import com.hezapp.ekonomis.steps.FillTransactionFormSteps

/**
 * Gabungan dari test utils
 */
class TestUiUtils(
    composeRule: ComposeTestRule,
    context: Context,
) : ITestUiUtils {
    //region ROBOT
    override val transactionHistoryRobot by lazy { TransactionHistoryRobot(composeRule, context) }
    override val transactionFormRobot by lazy { TransactionFormRobot(composeRule, context) }
    override val productPreviewRobot by lazy { ProductPreviewRobot(composeRule, context) }
    override val productDetailRobot by lazy { ProductDetailRobot(composeRule, context) }
    override val editProductNameDialogRobot by lazy { EditProductNameDialogRobot(composeRule, context) }
    //endregion

    //region STEPS
    override val fillTransactionSteps by lazy { FillTransactionFormSteps(transactionFormRobot) }
    //endregion
}

interface ITestUiUtils {
    val transactionHistoryRobot: TransactionHistoryRobot
    val transactionFormRobot: TransactionFormRobot
    val productPreviewRobot: ProductPreviewRobot
    val productDetailRobot: ProductDetailRobot
    val editProductNameDialogRobot: EditProductNameDialogRobot
    val fillTransactionSteps: FillTransactionFormSteps
}