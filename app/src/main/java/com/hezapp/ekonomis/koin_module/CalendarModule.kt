package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import org.koin.dsl.module

val CalendarModule = module {
    single<ITimeService> { TimeService() }
}