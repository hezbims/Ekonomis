package com.hezapp.ekonomis._koin_module

import com.hezapp.ekonomis.core.application.utils.IDispatcherProvider
import com.hezapp.ekonomis.core.data.utils.DispatcherProvider
import com.hezapp.ekonomis.core.data.utils.FirebaseErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import org.koin.dsl.module

val UtilitiesModule = module {
    single<ITimeService> { TimeService() }
    single<IErrorReportingService> { FirebaseErrorReportingService() }
    single<IDispatcherProvider> { DispatcherProvider() }
}