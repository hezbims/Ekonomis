package com.hezapp.ekonomis.test_application

import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.robot.transaction_form.TransactionFormRobot
import com.hezapp.ekonomis.robot.TransactionHistoryRobot
import com.hezapp.ekonomis.test_data.TestTimeService
import org.junit.After
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
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
    @get:Rule(order = 3)
    val logErrorRule = object : TestWatcher() {
        override fun failed(e: Throwable?, description: Description?) {
            composeRule.onAllNodes(isRoot()).printToLog(
                tag = "QQQ Last Tree",
                maxDepth = Int.MAX_VALUE)
            super.failed(e, description)
        }
    }

    protected val transactionHistoryRobot by lazy { TransactionHistoryRobot(composeRule) }
    protected val transactionFormRobot by lazy { TransactionFormRobot(composeRule) }


    @After
    fun after(){
        stopKoin()
        TestTimeService.reset()
    }
}