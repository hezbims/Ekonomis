package com.hezapp.ekonomis.utils_aggregate

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.app_test_utils.add_or_update_transaction.presentation.robot.TransactionFormRobot
import com.hezapp.ekonomis.app_test_utils.edit_product_name_dialog.presentation.robot.EditProductNameDialogRobot
import com.hezapp.ekonomis.app_test_utils.product_detail.presentation.robot.EditCurrentMonthlyStockDialogRobot
import com.hezapp.ekonomis.app_test_utils.product_detail.presentation.robot.ProductDetailRobot
import com.hezapp.ekonomis.app_test_utils.product_preview.presentation.robot.ProductPreviewRobot
import com.hezapp.ekonomis.app_test_utils.transaction_history.presentation.robot.TransactionHistoryRobot
import com.hezapp.ekonomis.utils_aggregate.steps.FillTransactionFormSteps
import org.koin.core.Koin
import org.koin.core.context.GlobalContext

/**
 * Gabungan dari test utils
 */
class TestUiUtils(
    composeRule: ComposeTestRule,
    context: Context,
    koin: Koin = GlobalContext.get(),
) : ITestUiUtils {
    //region ROBOT
    override val transactionHistoryRobot by lazy { TransactionHistoryRobot(composeRule, context) }
    override val transactionFormRobot by lazy { TransactionFormRobot(composeRule, context) }
    override val productPreviewRobot by lazy { ProductPreviewRobot(composeRule, context) }
    override val productDetailRobot by lazy { ProductDetailRobot(composeRule, context, timeService = koin.get()) }
    override val editMonthlyStockDialogRobot by lazy { EditCurrentMonthlyStockDialogRobot(composeRule, context) }
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
    val editMonthlyStockDialogRobot: EditCurrentMonthlyStockDialogRobot
    val fillTransactionSteps: FillTransactionFormSteps
}