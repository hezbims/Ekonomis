package com.hezapp.ekonomis.test_application

import android.content.Context
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.robot.transaction_form.TransactionFormRobot
import com.hezapp.ekonomis.robot.TransactionHistoryRobot
import com.hezapp.ekonomis.steps.FillTransactionFormSteps
import com.hezapp.ekonomis.test_utils.TestTimeService
import com.hezapp.ekonomis.test_utils.seeder.InvoiceSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProductSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProfileSeeder
import org.junit.After
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import java.util.Locale
import java.util.TimeZone

@RunWith(AndroidJUnit4::class)
abstract class BaseEkonomisIntegrationTest {
    @get:Rule(order = 1)
    val timeConfigSetup = object : ExternalResource(){
        override fun before() {
            val locale = Locale.forLanguageTag("id-ID") // Indonesia
            Locale.setDefault(locale)

            // enggak ngubah context seluruh app
//        val config = Configuration(composeRule.activity.resources.configuration)
//        config.setLocale(locale)
//        val context = composeRule.activity.createConfigurationContext(config)

            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"))
            super.before()
        }
    }
    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()
    private val context : Context
        get() = composeRule.activity
    @get:Rule(order = 3)
    val logErrorRule = object : TestWatcher() {
        override fun failed(e: Throwable?, description: Description?) {
            composeRule.onAllNodes(isRoot()).printToLog(
                tag = "QQQ Last Tree",
                maxDepth = Int.MAX_VALUE)
            super.failed(e, description)
        }
    }

    //region ROBOT
    protected val transactionHistoryRobot by lazy { TransactionHistoryRobot(composeRule, context) }
    protected val transactionFormRobot by lazy { TransactionFormRobot(composeRule, context) }
    //endregion

    //region STEPS
    protected val filltransactionSteps by lazy { FillTransactionFormSteps(transactionFormRobot) }
    //endregion

    //region SEEDER
    protected val invoiceSeeder = InvoiceSeeder()
    protected val productSeeder = ProductSeeder()
    protected val profileSeeder = ProfileSeeder()
    //endregion

    @After
    fun after(){
        TestTimeService.reset()
    }
}