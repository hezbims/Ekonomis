package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.core.domain.utils.CalendarProvider
import org.koin.dsl.module

val CalendarModule = module {
    single { CalendarProvider() }
}