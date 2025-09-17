package com.hezapp.ekonomis.test_application

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.platform.app.InstrumentationRegistry
import com.hezapp.ekonomis.MainApplication
import com.hezapp.ekonomis.koin_module.KoinProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MyKoinTestApplication : Application(), KoinProvider {
    private lateinit var _koinApp : KoinApplication
    override val koinApp: KoinApplication
        get() = _koinApp

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        if (GlobalContext.getOrNull() == null) {
            val appContext =
                InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            _koinApp = startKoin {
                allowOverride(true)
                androidContext(appContext)
                modules(MainApplication.Companion.koinModules)
            }
            loadTestKoinModules(appContext)
        }
    }
}