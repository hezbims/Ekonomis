package com.hezapp.ekonomis

import android.app.Application
import com.hezapp.ekonomis.koin_module.CalendarModule
import com.hezapp.ekonomis.koin_module.DaoModule
import com.hezapp.ekonomis.koin_module.DatabaseModule
import com.hezapp.ekonomis.koin_module.RepositoryModule
import com.hezapp.ekonomis.koin_module.UseCaseModule
import com.hezapp.ekonomis.koin_module.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(koinModules)
        }
    }

    companion object {
        val koinModules = listOf(
            DatabaseModule,
            CalendarModule,
            DaoModule,
            RepositoryModule,
            UseCaseModule,
            ViewModelModule,
        )
    }
}