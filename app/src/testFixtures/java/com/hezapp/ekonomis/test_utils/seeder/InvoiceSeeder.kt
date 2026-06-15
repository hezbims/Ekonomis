package com.hezapp.ekonomis.test_utils.seeder

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.withTransaction
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.data.installment.dao.InstallmentDao
import com.hezapp.ekonomis.core.data.installment_item.dao.InstallmentItemDao
import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.data.invoice_item.dao.InvoiceItemDao
import com.hezapp.ekonomis.core.data.product.dao.ProductDao
import com.hezapp.ekonomis.core.data.profile.dao.ProfileDao
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment
import com.hezapp.ekonomis.core.domain.invoice.entity.InstallmentItem
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InstallmentItemSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InstallmentSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InvoiceItemSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InvoiceSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.ProductSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.ProfileSnapshot
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import java.time.LocalDate

class InvoiceSeeder(
    koin: Koin = GlobalContext.get(),
) {
    private val invoiceDao: InvoiceDao = koin.get()
    private val invoiceItemDao: InvoiceItemDao = koin.get()
    private val installmentDao: InstallmentDao = koin.get()
    private val installmentItemDao: InstallmentItemDao = koin.get()
    private val profileDao : ProfileDao = koin.get()
    private val productDao : ProductDao = koin.get()
    private val database: EkonomisDatabase = koin.get()
    private val timeService: ITimeService = koin.get()

    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated(
        "Use DSL for readability instead",
        replaceWith = ReplaceWith(
            "com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.thereIsTransactionOn"
        )
    )
    suspend fun run(
        profileId: Int,
        date: LocalDate,
        invoiceItems: List<InvoiceItemSeed>,
        ppn: Int?,
        installmentSeed: InstallmentSeed? = null,
        paymentMedia: PaymentMedia = PaymentMedia.TRANSFER,
    ) : InvoiceSnapshot {
        val profile = profileDao.getProfilesByIds(listOf(profileId)).singleOrNull() ?:
            throw RuntimeException("No profile with id '${profileId}' found")

        return run(
            profile = profile,
            date = date,
            invoiceItems = invoiceItems,
            ppn = ppn,
            installmentSeed = installmentSeed,
            paymentMedia = paymentMedia,
        )
    }


    @Deprecated(
        "Use DSL for readability instead",
        replaceWith = ReplaceWith(
            "com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.thereIsTransactionOn"
        )
    )
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun run(
        profile: ProfileEntity,
        date: LocalDate,
        invoiceItems: List<InvoiceItemSeed>,
        ppn: Int?,
        installmentSeed: InstallmentSeed? = null,
        paymentMedia: PaymentMedia = PaymentMedia.TRANSFER,
    ) : InvoiceSnapshot {
        val transactionType = when(profile.type){
            ProfileType.SUPPLIER -> TransactionType.PEMBELIAN
            ProfileType.CUSTOMER -> TransactionType.PENJUALAN
        }
        if (ppn == null && transactionType == TransactionType.PEMBELIAN)
            throw IllegalArgumentException("Transaksi pembelian harus memiliki ppn")

        return database.withTransaction {
            val invoiceId = invoiceDao.upsertInvoice(InvoiceEntity(
                date = date.atStartOfDay(
                    timeService.getZoneId()
                ).toInstant().toEpochMilli(),
                ppn = if (transactionType == TransactionType.PENJUALAN) null else ppn!!,
                profileId = profile.id,
                transactionType = transactionType,
                paymentMedia = paymentMedia,
            )).toInt()

            invoiceItems.forEach {
                productDao.getProduct(it.productId) ?:
                    throw RuntimeException("Product with id '${it.productId}' not found in DB")

                invoiceItemDao.upsertInvoiceItems(
                    InvoiceItemEntity(
                        productId = it.productId,
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
                        paymentMedia = item.paymentMedia,
                    )
                }

                installmentItemDao.insert(mappedItems)
            }

            val fullDetails = invoiceDao.getFullInvoiceDetails(invoiceId)

            InvoiceSnapshot(
                id = fullDetails.invoice.invoice.id,
                dateInMillis = fullDetails.invoice.invoice.date,
                ppn = fullDetails.invoice.invoice.ppn,
                transactionType = fullDetails.invoice.invoice.transactionType,
                paymentMedia = fullDetails.invoice.invoice.paymentMedia,
                profile = ProfileSnapshot(
                    id = fullDetails.profile.id,
                    name = fullDetails.profile.name,
                    type = fullDetails.profile.type,
                ),
                invoiceItems = fullDetails.invoice.invoiceItemWithProducts.map { itemWithProduct ->
                    InvoiceItemSnapshot(
                        id = itemWithProduct.invoiceItem.id,
                        product = ProductSnapshot(
                            id = itemWithProduct.product.id,
                            name = itemWithProduct.product.name,
                        ),
                        quantity = itemWithProduct.invoiceItem.quantity,
                        price = itemWithProduct.invoiceItem.price,
                        unitType = itemWithProduct.invoiceItem.unitType,
                    )
                },
                installment = fullDetails.installmentWithItems?.let { installmentWithItems ->
                    InstallmentSnapshot(
                        id = installmentWithItems.installment.id,
                        isPaidOff = installmentWithItems.installment.isPaidOff,
                        items = installmentWithItems.installmentItems.map { item ->
                            InstallmentItemSnapshot(
                                id = item.id,
                                amount = item.amount,
                                paymentDate = item.paymentDate,
                                paymentMedia = item.paymentMedia,
                            )
                        },
                    )
                },
            )
        }
    }
}

data class InvoiceItemSeed(
    val quantity: Int,
    val unitType: UnitType,
    val productId: Int,
    val price: Int,
) {
    @Deprecated(message = "Use primary constructor instead")
    constructor(
        quantity: Int,
        unitType: UnitType,
        product: ProductEntity,
        price: Int,
    ) : this(
        quantity = quantity,
        unitType = unitType,
        productId = product.id,
        price = price,
    )
}

data class InstallmentSeed(
    val isPaidOff: Boolean,
    val items: List<InstallmentItemSeed>,
)

data class InstallmentItemSeed(
    val amount: Int,
    val paymentDate: LocalDate,
    val paymentMedia: PaymentMedia = PaymentMedia.TRANSFER,
)