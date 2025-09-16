package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.core.data.utils.FirebaseErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import org.koin.dsl.module

val UtilitiesModule = module {
    single<ITimeService> { TimeService() }
    single<IErrorReportingService> { FirebaseErrorReportingService() }
}