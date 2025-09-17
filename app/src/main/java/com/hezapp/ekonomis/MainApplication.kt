package com.hezapp.ekonomis

import android.app.Application
import com.hezapp.ekonomis.koin_module.UtilitiesModule
import com.hezapp.ekonomis.koin_module.DaoModule
import com.hezapp.ekonomis.koin_module.DatabaseModule
import com.hezapp.ekonomis.koin_module.KoinProvider
import com.hezapp.ekonomis.koin_module.RepositoryModule
import com.hezapp.ekonomis.koin_module.UseCaseModule
import com.hezapp.ekonomis.koin_module.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

class MainApplication : Application(), KoinProvider {
    private lateinit var _koinApp : KoinApplication
    override val koinApp: KoinApplication
        get() = _koinApp

    override fun onCreate() {
        super.onCreate()
        _koinApp = startKoin {
            androidContext(this@MainApplication)
            modules(koinModules)
        }
    }

    companion object {
        val koinModules = listOf(
            DatabaseModule,
            UtilitiesModule,
            DaoModule,
            RepositoryModule,
            UseCaseModule,
            ViewModelModule,
        )
    }
}