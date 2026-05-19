package com.hezapp.ekonomis.test_application

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hezapp.ekonomis.test_utils.TestDataUtils
import com.hezapp.ekonomis.test_utils.gherkin.IGherkinSyntax
import com.hezapp.ekonomis.test_utils.rule.GlobalTimeConfigRule
import com.hezapp.ekonomis.test_utils.rule.KoinRule
import com.hezapp.ekonomis.test_utils.rule.TestEnvironmentResetRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.robolectric.shadows.ShadowLog

@RunWith(AndroidJUnit4::class)
abstract class BaseDataUnitTest(
    private val loadDefaultKoinModules : Boolean = true
) : IGherkinSyntax {
    private var _koinApp: KoinApplication? = null
    protected val koinApp: KoinApplication
        get() = _koinApp!!
    protected val koin: Koin
        get() = _koinApp!!.koin
    protected val appContext : Context = ApplicationProvider.getApplicationContext()

    init {
        ShadowLog.stream = System.out
    }

    @get:Rule(order = 0)
    val koinAppRule = KoinRule(
        appContext = appContext,
        onKoinApplicationCreated = { koinApp -> _koinApp = koinApp},
        options = KoinRule.KoinOptions(
            loadDefaultKoinModules = loadDefaultKoinModules
        )
    )

    @get:Rule(1)
    val globalTimeConfigRule = GlobalTimeConfigRule()

    @get:Rule(2)
    val testEnvironementResetRule = TestEnvironmentResetRule { koin }

    val dataUtils by lazy {
        TestDataUtils(koin = koin)
    }
}