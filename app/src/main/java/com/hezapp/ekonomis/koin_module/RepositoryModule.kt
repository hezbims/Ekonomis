package com.hezapp.ekonomis.koin_module

import com.hezapp.ekonomis.core.data.invoice.repo.InvoiceRepo
import com.hezapp.ekonomis.core.data.invoice_item.repo.InvoiceItemRepo
import com.hezapp.ekonomis.core.data.monthly_stock.repo.MonthlyStockRepo
import com.hezapp.ekonomis.core.data.product.repo.ProductRepo
import com.hezapp.ekonomis.core.data.profile.repo.ProfileRepo
import com.hezapp.ekonomis.core.domain.invoice.repo.IInvoiceRepo
import com.hezapp.ekonomis.core.domain.invoice_item.repo.IInvoiceItemRepo
import com.hezapp.ekonomis.core.domain.monthly_stock.repo.IMonthlyStockRepo
import com.hezapp.ekonomis.core.domain.product.repo.IProductRepo
import com.hezapp.ekonomis.core.domain.profile.repo.IProfileRepo
import org.koin.dsl.module

val RepositoryModule = module {
    single<IProfileRepo> { ProfileRepo(dao = get()) }
    single<IProductRepo> { ProductRepo(dao = get()) }
    single<IInvoiceRepo> { InvoiceRepo(dao = get()) }
    single<IInvoiceItemRepo> { InvoiceItemRepo(dao = get()) }
    single<IMonthlyStockRepo> { MonthlyStockRepo(dao = get()) }
}