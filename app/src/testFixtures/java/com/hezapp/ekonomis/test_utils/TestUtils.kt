package com.hezapp.ekonomis.test_utils

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.robot.ProductDetailRobot
import com.hezapp.ekonomis.robot.ProductPreviewRobot
import com.hezapp.ekonomis.robot.transaction_history.TransactionHistoryRobot
import com.hezapp.ekonomis.robot.transaction_form.TransactionFormRobot
import com.hezapp.ekonomis.steps.FillTransactionFormSteps
import com.hezapp.ekonomis.test_utils.db_assertion.MasterDataDbAssertion
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDbAssertion
import com.hezapp.ekonomis.test_utils.seeder.InvoiceSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProductSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProfileSeeder
import org.koin.core.Koin

/**
 * Gabungan dari test utils
 */
class TestUtils(
    composeRule: ComposeTestRule,
    context: Context,
    koin: Koin,
) {
    //region ROBOT
    val transactionHistoryRobot by lazy { TransactionHistoryRobot(composeRule, context) }
    val transactionFormRobot by lazy { TransactionFormRobot(composeRule, context) }
    val productPreviewRobot by lazy { ProductPreviewRobot(composeRule, context) }
    val productDetailRobot by lazy { ProductDetailRobot(composeRule, context) }
    //endregion

    //region STEPS
    val filltransactionSteps by lazy { FillTransactionFormSteps(transactionFormRobot) }
    //endregion

    //region SEEDER
    val invoiceSeeder = InvoiceSeeder(koin)
    val productSeeder = ProductSeeder(koin)
    val profileSeeder = ProfileSeeder(koin)
    //endregion

    //region DB ASSERTION
    val transactionDbAssertion by lazy { TransactionDbAssertion(koin) }
    val masterDataDbAssertion by lazy { MasterDataDbAssertion(koin) }
    //endregion
}