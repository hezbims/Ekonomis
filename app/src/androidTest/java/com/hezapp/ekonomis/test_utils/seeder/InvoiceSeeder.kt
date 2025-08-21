package com.hezapp.ekonomis.test_utils.seeder

import androidx.room.withTransaction
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.data.installment.dao.InstallmentDao
import com.hezapp.ekonomis.core.data.installment_item.dao.InstallmentItemDao
import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.data.invoice_item.dao.InvoiceItemDao
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment
import com.hezapp.ekonomis.core.domain.invoice.entity.InstallmentItem
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.test_utils.TestTimeService
import org.koin.core.context.GlobalContext
import java.time.LocalDate

class InvoiceSeeder(
    private val invoiceDao: InvoiceDao = GlobalContext.get().get(),
    private val invoiceItemDao: InvoiceItemDao = GlobalContext.get().get(),
    private val installmentDao: InstallmentDao = GlobalContext.get().get(),
    private val installmentItemDao: InstallmentItemDao = GlobalContext.get().get(),
    private val database: EkonomisDatabase = GlobalContext.get().get(),
) {
    suspend fun run(
        profile: ProfileEntity,
        date: LocalDate,
        invoiceItems: List<InvoiceItemSeed>,
        ppn: Int?,
        installmentSeed: InstallmentSeed? = null,
    ){
        val transactionType = when(profile.type){
            ProfileType.SUPPLIER -> TransactionType.PEMBELIAN
            ProfileType.CUSTOMER -> TransactionType.PENJUALAN
        }
        if (ppn == null && transactionType == TransactionType.PEMBELIAN)
            throw IllegalArgumentException("Transaksi pembelian harus memiliki ppn")

        database.withTransaction {
            val invoiceId = invoiceDao.upsertInvoice(InvoiceEntity(
                date = date.atStartOfDay(
                    TestTimeService.get().getZoneId()
                ).toInstant().toEpochMilli(),
                ppn = if (transactionType == TransactionType.PENJUALAN) null else ppn!!,
                profileId = profile.id,
                transactionType = transactionType,
            )).toInt()

            invoiceItems.forEach {
                invoiceItemDao.upsertInvoiceItems(
                    InvoiceItemEntity(
                        productId = it.product.id,
                        invoiceId = invoiceId,
                        quantity = it.quantity,
                        price = it.price,
                        unitType = it.unitType,
                    )
                )
            }

            installmentSeed?.let { installmentSeed ->
                val installmentId = installmentDao.insert(Installment(
                    invoiceId = invoiceId,
                    isPaidOff = installmentSeed.isPaidOff,
                ))

                val mappedItems = installmentSeed.items.map { item ->
                    InstallmentItem(
                        installmentId = installmentId.toInt(),
                        paymentDate = item.paymentDate,
                        amount = item.amount,
                    )
                }

                installmentItemDao.insert(mappedItems)
            }
        }
    }
}

data class InvoiceItemSeed(
    val quantity: Int,
    val unitType: UnitType,
    val product: ProductEntity,
    val price: Int,
)

data class InstallmentSeed(
    val isPaidOff: Boolean,
    val items: List<InstallmentItemSeed>,
)

data class InstallmentItemSeed(
    val amount: Int,
    val paymentDate: LocalDate,
)