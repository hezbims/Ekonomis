package com.hezapp.ekonomis.test_application

import android.util.Log
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.printToLog
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.robot.ProductDetailRobot
import com.hezapp.ekonomis.robot.ProductPreviewRobot
import com.hezapp.ekonomis.robot.TransactionHistoryRobot
import com.hezapp.ekonomis.robot.transaction_form.TransactionFormRobot
import com.hezapp.ekonomis.steps.FillTransactionFormSteps
import com.hezapp.ekonomis.test_utils.TestTimeService
import com.hezapp.ekonomis.test_utils.db_assertion.MasterDataDbAssertion
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDbAssertion
import com.hezapp.ekonomis.test_utils.seeder.InvoiceSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProductSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProfileSeeder
import org.junit.Before
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import java.util.Locale
import java.util.TimeZone

/**
 * @param immediatelyLaunchMainActivity gunakan ini dengan nilai `true` untuk langsung
 * launch MainActivity sebelum semua `@Before` dijalankan. Namun untuk test-test yang
 * menggunakan `seeder`, sebaiknya hanya launch `MainActivity` jika
 * semua `seeder` di `@Before` selesai dijalankan. Kalau seeder belum selesai,
 * namun DAO yang berada di viewmodel sudah mencoba mengakses test data
 * (misal karena karena premature launch activity), maka bisa terjadi unexpected behaviour.
 */
@RunWith(AndroidJUnit4::class)
abstract class BaseEkonomisUiTest(
    private val immediatelyLaunchMainActivity : Boolean = false,
) {
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
    val composeRule = createEmptyComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    @get:Rule(order = 3)
    val logErrorRule = object : TestWatcher() {
        override fun failed(e: Throwable?, description: Description?) {
            try {
                composeRule.onAllNodes(isRoot()).printToLog(
                    tag = "QQQ Last Tree",
                    maxDepth = Int.MAX_VALUE
                )
            } catch (t : Throwable) {
                Log.e("QQQ error", "Error printing semantics tree at the end of test " +
                        (t.message ?: "Unknown error"))
            }
            super.failed(e, description)
        }
    }

    //region ROBOT
    protected val transactionHistoryRobot by lazy {
        TransactionHistoryRobot(
            composeRule,
            context
        )
    }
    protected val transactionFormRobot by lazy { TransactionFormRobot(composeRule, context) }
    protected val productPreviewRobot by lazy { ProductPreviewRobot(composeRule, context) }
    protected val productDetailRobot by lazy { ProductDetailRobot(composeRule, context) }
    //endregion

    //region STEPS
    protected val filltransactionSteps by lazy { FillTransactionFormSteps(transactionFormRobot) }
    //endregion

    //region SEEDER
    protected val invoiceSeeder = InvoiceSeeder()
    protected val productSeeder = ProductSeeder()
    protected val profileSeeder = ProfileSeeder()
    //endregion

    //region DB ASSERTION
    protected val transactionDbAssertion by lazy { TransactionDbAssertion() }
    protected val masterDataDbAssertion by lazy { MasterDataDbAssertion() }
    //endregion

    @Before
    fun reset(){
        GlobalContext.get().get<EkonomisDatabase>().clearAllTables()
        TestTimeService.Companion.reset()
        if (immediatelyLaunchMainActivity)
            ActivityScenario.launch(MainActivity::class.java)
    }
}