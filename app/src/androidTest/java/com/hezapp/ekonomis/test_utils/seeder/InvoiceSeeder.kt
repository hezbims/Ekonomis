package com.hezapp.ekonomis.test_utils.seeder

import androidx.room.withTransaction
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.data.invoice_item.dao.InvoiceItemDao
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
    private val database: EkonomisDatabase = GlobalContext.get().get(),
) {
    suspend fun run(
        profile: ProfileEntity,
        date: LocalDate,
        invoiceItems: List<InvoiceItemSeed>,
        ppn: Int?,
    ){
        val isPenjualan = profile.type == ProfileType.CUSTOMER
        if (ppn == null && !isPenjualan)
            throw IllegalArgumentException("Transaksi pembelian harus memiliki ppn")

        database.withTransaction {
            val invoiceId = invoiceDao.upsertInvoice(InvoiceEntity(
                date = date.atStartOfDay(
                    TestTimeService.get().getZoneId()
                ).toInstant().toEpochMilli(),
                ppn = if (isPenjualan) ppn!! else null,
                profileId = profile.id,
                transactionType = if (isPenjualan) TransactionType.PENJUALAN else TransactionType.PEMBELIAN,
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
        }
    }
}

data class InvoiceItemSeed(
    val quantity: Int,
    val unitType: UnitType,
    val product: ProductEntity,
    val price: Int,
)