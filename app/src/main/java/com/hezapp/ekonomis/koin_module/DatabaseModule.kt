package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.data.database.TransactionProvider
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import org.koin.dsl.module

val DatabaseModule = module {
    single { EkonomisDatabase.getInstance(get()) }
    single<ITransactionProvider> { TransactionProvider(db = get()) }
}