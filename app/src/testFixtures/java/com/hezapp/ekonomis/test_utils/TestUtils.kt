package com.hezapp.ekonomis.test_utils

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.robot.ProductDetailRobot
import com.hezapp.ekonomis.robot.ProductPreviewRobot
import com.hezapp.ekonomis.robot.transaction_form.TransactionFormRobot
import com.hezapp.ekonomis.robot.transaction_history.TransactionHistoryRobot
import com.hezapp.ekonomis.steps.FillTransactionFormSteps
import com.hezapp.ekonomis.test_utils.db_assertion.MasterDataDbAssertion
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDbAssertion
import com.hezapp.ekonomis.test_utils.seeder.InvoiceSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProductSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProfileSeeder
import org.koin.core.Koin
import org.koin.core.context.GlobalContext

/**
 * Gabungan dari test utils
 */
class TestUtils(
    composeRule: ComposeTestRule,
    context: Context,
    val koin: Koin = GlobalContext.get(),
) : ITestUtils {
    //region ROBOT
    override val transactionHistoryRobot by lazy { TransactionHistoryRobot(composeRule, context) }
    override val transactionFormRobot by lazy { TransactionFormRobot(composeRule, context) }
    override val productPreviewRobot by lazy { ProductPreviewRobot(composeRule, context) }
    override val productDetailRobot by lazy { ProductDetailRobot(composeRule, context) }
    //endregion

    //region STEPS
    override val fillTransactionSteps by lazy { FillTransactionFormSteps(transactionFormRobot) }
    //endregion

    //region SEEDER
    override val invoiceSeeder = InvoiceSeeder(koin)
    override val productSeeder = ProductSeeder(koin)
    override val profileSeeder = ProfileSeeder(koin)
    //endregion

    //region DB ASSERTION
    override val transactionDbAssertion by lazy { TransactionDbAssertion(koin) }
    override val masterDataDbAssertion by lazy { MasterDataDbAssertion(koin) }
    //endregion
}

interface ITestUtils {
    val transactionHistoryRobot: TransactionHistoryRobot
    val transactionFormRobot: TransactionFormRobot
    val productPreviewRobot: ProductPreviewRobot
    val productDetailRobot: ProductDetailRobot
    val fillTransactionSteps: FillTransactionFormSteps
    val invoiceSeeder: InvoiceSeeder
    val productSeeder: ProductSeeder
    val profileSeeder: ProfileSeeder
    val transactionDbAssertion: TransactionDbAssertion
    val masterDataDbAssertion: MasterDataDbAssertion
}