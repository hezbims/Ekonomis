package com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.scope

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
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.test_utils.seeder.ProductSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProfileSeeder
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InstallmentItemSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InstallmentSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InvoiceItemSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InvoiceSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.ProductSnapshot
import com.hezapp.ekonomis.test_utils.seeder.snapshot.ProfileSnapshot
import org.koin.core.Koin
import java.time.YearMonth

/**
 * Scope to define transactions within a single month.
 *
 * Instances are created by [com.hezapp.ekonomis.test_utils.seeder.dsl.transaction.transactionOn]. Inside the lambda block,
 * call [out] for a sale transaction or [in] for a purchase transaction.
 */
@RequiresApi(Build.VERSION_CODES.O)
class OneMonthTransactionsScope(
    internal val yearMonth: YearMonth,
    internal val koin: Koin,
) {
    private val database: EkonomisDatabase = koin.get()
    private val invoiceDao: InvoiceDao = koin.get()
    private val invoiceItemDao: InvoiceItemDao = koin.get()
    private val installmentDao: InstallmentDao = koin.get()
    private val installmentItemDao: InstallmentItemDao = koin.get()
    private val productDao: ProductDao = koin.get()
    private val profileDao: ProfileDao = koin.get()
    private val timeService: ITimeService = koin.get()

    internal val snapshots = mutableListOf<InvoiceSnapshot>()

    /**
     * Creates a **PENJUALAN** (sale / goods-out) transaction.
     *
     * @param day day of the month (1-31), defaults to 1
     * @param profileId customer profile id.
     *   If `null`, looks for a profile named `"customer-default"`.
     *   If not found, creates one via [com.hezapp.ekonomis.test_utils.seeder.ProfileSeeder].
     * @param block lambda scope to define products and installment options
     */
    suspend fun out(
        day: Int = 1,
        profileId: Int? = null,
        block: suspend OneDayTransactionScope.() -> Unit,
    ) {
        val oneDayTransactionScope = OneDayTransactionScope(
            transactionType = TransactionType.PENJUALAN,
            yearMonth = yearMonth,
            day = day,
            ppn = null,
            profileId = profileId,
        )
        oneDayTransactionScope.block()
        oneDayTransactionScope.validate()
        seedOneTransaction(oneDayTransactionScope)
    }

    /**
     * Creates a **PEMBELIAN** (purchase / goods-in) transaction.
     *
     * @param day day of the month (1-31), defaults to 1
     * @param ppn tax percentage, defaults to 11. Required for purchases.
     * @param profileId supplier profile id.
     *   If `null`, looks for a profile named `"supplier-default"`.
     *   If not found, creates one via [com.hezapp.ekonomis.test_utils.seeder.ProfileSeeder].
     * @param block lambda scope to define products and installment options
     */
    suspend fun `in`(
        day: Int = 1,
        ppn: Int = 11,
        profileId: Int? = null,
        block: suspend OneDayTransactionScope.() -> Unit,
    ) {
        val oneDayTransactionScope = OneDayTransactionScope(
            transactionType = TransactionType.PEMBELIAN,
            yearMonth = yearMonth,
            day = day,
            ppn = ppn,
            profileId = profileId,
        )
        oneDayTransactionScope.block()
        oneDayTransactionScope.validate()
        seedOneTransaction(oneDayTransactionScope)
    }

    private suspend fun seedOneTransaction(scope: OneDayTransactionScope) {
        val date = scope.yearMonth.atDay(scope.day)
        val resolvedProfileId = resolveExistingProfile(scope)

        if (scope.ppn == null && scope.transactionType == TransactionType.PEMBELIAN)
            throw IllegalArgumentException("Purchase transaction must have a PPN value")

        val snapshot = database.withTransaction {
            val invoiceId = invoiceDao.upsertInvoice(
                invoice = InvoiceEntity(
                    date = date.atStartOfDay(timeService.getZoneId())
                        .toInstant().toEpochMilli(),
                    ppn = if (scope.transactionType == TransactionType.PENJUALAN) null else scope.ppn!!,
                    profileId = resolvedProfileId,
                    transactionType = scope.transactionType,
                    paymentMedia = scope.paymentMedia,
                )
            ).toInt()

            scope.productSeeds.forEach { productSeed ->
                val productId = productSeed.id ?: run {
                    val allProducts = productDao.getAllProducts(searchQuery = "")
                    if (allProducts.isNotEmpty()) {
                        allProducts.first().id
                    } else {
                        ProductSeeder(koin).runV2(name = "product-default").id
                    }
                }
                productDao.getProduct(id = productId)
                    ?: throw RuntimeException("Product with id '$productId' not found in DB")
                invoiceItemDao.upsertInvoiceItems(
                    InvoiceItemEntity(
                        productId = productId,
                        invoiceId = invoiceId,
                        quantity = productSeed.quantity.amount,
                        price = productSeed.price,
                        unitType = productSeed.quantity.unitType,
                    )
                )
            }

            scope.installmentSeeds?.let { data ->
                val isPaidOff = data.isPaidOff ?: run {
                    val totalInvoice = scope.productSeeds.sumOf { it.quantity.amount * it.price }
                    val totalPayments = data.paymentSeeds.sumOf { it.amount }
                    totalPayments >= totalInvoice
                }
                val installmentId = installmentDao.insert(
                    data = Installment(
                        invoiceId = invoiceId,
                        isPaidOff = isPaidOff,
                    )
                )
                val mappedPaymentItems = data.paymentSeeds.map { payment ->
                    InstallmentItem(
                        installmentId = installmentId.toInt(),
                        paymentDate = payment.date,
                        amount = payment.amount,
                        paymentMedia = payment.media,
                    )
                }
                installmentItemDao.insert(datas = mappedPaymentItems)
            }

            val fullDetails = invoiceDao.getFullInvoiceDetails(id = invoiceId)
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
        snapshots.add(snapshot)
    }

    private suspend fun resolveExistingProfile(scope: OneDayTransactionScope): Int {
        if (scope.profileId != null) {
            profileDao.getProfilesByIds(ids = listOf(scope.profileId)).singleOrNull()
                ?: throw RuntimeException("Profile with id '${scope.profileId}' not found")
            return scope.profileId
        }
        val (defaultName, defaultType) = when (scope.transactionType) {
            TransactionType.PEMBELIAN -> "supplier-default" to ProfileType.SUPPLIER
            TransactionType.PENJUALAN -> "customer-default" to ProfileType.CUSTOMER
        }
        val existing = profileDao.getListProfile(
            profileName = defaultName,
            profileTypeId = defaultType.id,
        ).firstOrNull()
        if (existing != null) return existing.id

        return ProfileSeeder(koin).runV2(
            profileName = defaultName,
            profileType = defaultType,
        ).id
    }
}