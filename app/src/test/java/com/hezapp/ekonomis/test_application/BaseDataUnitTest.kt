package com.hezapp.ekonomis.test_application

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hezapp.ekonomis.test_utils.ITestDataUtils
import com.hezapp.ekonomis.test_utils.TestDataUtils
import com.hezapp.ekonomis.test_utils.gherkin.IGherkinSyntax
import com.hezapp.ekonomis.test_utils.rule.GlobalTimeConfigRule
import com.hezapp.ekonomis.test_utils.rule.KoinRule
import com.hezapp.ekonomis.test_utils.rule.TestEnvironmentResetRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.robolectric.shadows.ShadowLog

/// Using private constructor so [koinApp] won't be able to passed from test cases
@RunWith(AndroidJUnit4::class)
abstract class BaseDataUnitTest private constructor(
    loadDefaultKoinModules : Boolean,
    private var koinApp : KoinApplication,
) : IGherkinSyntax,
    ITestDataUtils by TestDataUtils(koinApp.koin)
{

    constructor(loadDefaultKoinModules: Boolean = true) :  this(
        loadDefaultKoinModules = loadDefaultKoinModules,
        koinApp = koinApplication()
    )

    protected val koin: Koin get() = koinApp.koin
    protected val appContext : Context = ApplicationProvider.getApplicationContext()

    init {
        ShadowLog.stream = System.out
    }

    @get:Rule(order = 0)
    val koinAppRule = KoinRule(
        appContext = appContext,
        koinApp = koinApp,
        options = KoinRule.KoinOptions(
            loadDefaultKoinModules = loadDefaultKoinModules
        )
    )

    @get:Rule(1)
    val globalTimeConfigRule = GlobalTimeConfigRule()

    @get:Rule(2)
    val testEnvironementResetRule = TestEnvironmentResetRule { koin }

    @Deprecated(message = "all utils can be immediately accessed in this class because of the delegation")
    val dataUtils by lazy { TestDataUtils(koin = koin) }
}