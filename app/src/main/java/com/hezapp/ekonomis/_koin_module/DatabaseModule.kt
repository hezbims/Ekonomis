package com.hezapp.ekonomis._koin_module

import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.data.database.TransactionProvider
import com.hezapp.ekonomis.core.domain.utils.ITransactionProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val DatabaseModule = module {
    single { EkonomisDatabase.getInstance(androidContext(), EkonomisDatabase::class.java) }
    single<ITransactionProvider> { TransactionProvider(db = get()) }
}