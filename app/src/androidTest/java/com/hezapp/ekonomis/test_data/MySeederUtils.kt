package com.hezapp.ekonomis.test_data

import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.data.invoice_item.dao.InvoiceItemDao
import com.hezapp.ekonomis.core.data.product.dao.ProductDao
import com.hezapp.ekonomis.core.data.profile.dao.ProfileDao
import kotlinx.coroutines.test.runTest
import org.koin.core.context.GlobalContext

class MySeederUtils {
    companion object {
        private val invoiceItemDao : InvoiceItemDao by lazy {
            GlobalContext.get().get()
        }
        private val invoiceDao : InvoiceDao by lazy {
            GlobalContext.get().get()
        }
        private val productDao : ProductDao by lazy {
            GlobalContext.get().get()
        }
        private val profileDao : ProfileDao by lazy {
            GlobalContext.get().get()
        }

        fun seedTigaBulanTransaksi() = runTest {
            supplierProfiles.forEach {
                profileDao.insertProfile(it)
            }
            pembeliProfiles.forEach {
                profileDao.insertProfile(it)
            }
            products.forEach {
                productDao.insertNewProduct(it)
            }
            seedInvoiceTestDatas(curMonthInvoices)
            seedInvoiceTestDatas(prevMonthInvoices)
            seedInvoiceTestDatas(nextMonthInvoices)
        }

        private suspend fun seedInvoiceTestDatas(
            testDatas : List<InvoiceTestData>
        ){
            testDatas.forEach {
                val invoiceId = invoiceDao.upsertInvoice(it.invoice).toInt()
                assert(invoiceId != -1)
                invoiceItemDao.upsertInvoiceItems(
                    *(it.invoiceItem.map { invoiceItem ->
                        invoiceItem.copy(invoiceId = invoiceId)
                    }.toTypedArray())
                )
            }
        }
    }
}