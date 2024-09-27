package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.data.invoice_item.dao.InvoiceItemDao
import com.hezapp.ekonomis.core.data.monthly_stock.dao.MonthlyStockDao
import com.hezapp.ekonomis.core.data.product.dao.ProductDao
import com.hezapp.ekonomis.core.data.profile.dao.ProfileDao
import org.koin.dsl.module

val DaoModule = module {
    single<ProfileDao> {
        get<EkonomisDatabase>().profileDao
    }
    single<ProductDao>{
        get<EkonomisDatabase>().productDao
    }
    single<InvoiceDao> {
        get<EkonomisDatabase>().invoiceDao
    }
    single<InvoiceItemDao>{
        get<EkonomisDatabase>().invoiceItemDao
    }
    single<MonthlyStockDao>{
        get<EkonomisDatabase>().monthlyStockDao
    }
}