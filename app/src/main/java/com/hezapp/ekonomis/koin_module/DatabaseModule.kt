package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import org.koin.dsl.module

val DatabaseModule = module {
    single { EkonomisDatabase.getInstance(get()) }
}