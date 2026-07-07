package com.hezapp.ekonomis.test_utils

import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.getNextMonthYear
import com.hezapp.ekonomis.core.domain.utils.getPreviousMonthYear
import java.util.Calendar

val ITimeService.curMonth: Long
    get() = getCalendar().timeInMillis

val ITimeService.prevMonth: Long
    get() = curMonth.getPreviousMonthYear(this)

val ITimeService.nextMonth: Long
    get() = curMonth.getNextMonthYear(this)

private fun Long.toNextDay(totalDay: Int, timeService: ITimeService) : Long {
    val calendar = timeService.getCalendar().apply {
        timeInMillis = this@toNextDay
        set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + totalDay)
    }
    return calendar.timeInMillis
}

data class InvoiceTestData(
    val invoice: InvoiceEntity,
    val invoiceItem: List<InvoiceItemEntity>
)

val supplierProfiles = List(2){
    ProfileEntity(
        id = it + 1,
        name = "Penjual ${it + 1}",
        type = ProfileType.SUPPLIER
    )
}

val pembeliProfiles = List(2) {
    val nomor = it + 1 + supplierProfiles.size
    ProfileEntity(
        id  = nomor,
        name = "Pembeli $nomor",
        type = ProfileType.CUSTOMER,
    )
}

val products = List(2) {
    ProductEntity(
        id = it + 1,
        name = "Barang ${it + 1}"
    )
}

fun getCurrentMonthInvoices(timeService: ITimeService) : List<InvoiceTestData> {
    return listOf(
        InvoiceTestData(
            invoice = InvoiceEntity(
                id = 1,
                date = timeService.curMonth.toNextDay(4, timeService),
                transactionType = TransactionType.PEMBELIAN,
                ppn = 11,
                profileId = supplierProfiles[0].id,
            ),
            invoiceItem = listOf(
                InvoiceItemEntity(
                    id = 0,
                    productId = products[0].id,
                    invoiceId = 0,
                    quantity = 5,
                    price = 10_000,
                    unitType = UnitType.PIECE,
                ),
                InvoiceItemEntity(
                    id = 0,
                    productId = products[1].id,
                    invoiceId = 0,
                    quantity = 6,
                    price = 60_000,
                    unitType = UnitType.CARTON,
                )
            )
        ),

        InvoiceTestData(
            invoice = InvoiceEntity(
                id = 2,
                date = timeService.curMonth.toNextDay(3, timeService),
                transactionType = TransactionType.PENJUALAN,
                ppn = null,
                profileId = pembeliProfiles[0].id,
            ),
            invoiceItem = listOf(
                InvoiceItemEntity(
                    id = 0,
                    productId = products[0].id,
                    invoiceId = 0,
                    quantity = 1,
                    price = 20_000,
                    unitType = UnitType.PIECE,
                ),
            )
        ),

        InvoiceTestData(
            invoice = InvoiceEntity(
                id = 3,
                date = timeService.curMonth.toNextDay(2, timeService),
                transactionType = TransactionType.PEMBELIAN,
                ppn = 10,
                profileId = supplierProfiles[1].id
            ),
            invoiceItem = listOf(
                InvoiceItemEntity(
                    id = 0,
                    productId = products[0].id,
                    invoiceId = 0,
                    quantity = 2,
                    price = 5000,
                    unitType = UnitType.CARTON,
                ),
                InvoiceItemEntity(
                    id = 0,
                    productId = products[1].id,
                    invoiceId = 0,
                    quantity = 5,
                    price = 50000,
                    unitType = UnitType.PIECE,
                )
            ),
        ),

        InvoiceTestData(
            invoice = InvoiceEntity(
                id = 4,
                date = timeService.curMonth.toNextDay(1, timeService),
                transactionType = TransactionType.PENJUALAN,
                ppn = null,
                profileId = pembeliProfiles[1].id,
            ),
            invoiceItem = listOf(
                InvoiceItemEntity(
                    id = 0,
                    invoiceId = 0,
                    price = 350_000,
                    productId = products[1].id,
                    quantity = 4,
                    unitType = UnitType.PIECE,
                )
            )
        )
    )
}

fun getPrevMonthInvoices(timeService: ITimeService) : List<InvoiceTestData> {
    return listOf(
        InvoiceTestData(
            invoice = InvoiceEntity(
                id = 5,
                date = timeService.prevMonth.toNextDay(-1, timeService),
                transactionType = TransactionType.PEMBELIAN,
                ppn = 10,
                profileId = supplierProfiles[0].id,
            ),
            invoiceItem = listOf(
                InvoiceItemEntity(
                    id = 0,
                    invoiceId = 0,
                    price = 250000,
                    quantity = 50,
                    productId = products[0].id,
                    unitType = UnitType.PIECE,
                )
            )
        ),
        InvoiceTestData(
            invoice = InvoiceEntity(
                id = 6,
                date = timeService.prevMonth.toNextDay(1, timeService),
                transactionType = TransactionType.PENJUALAN,
                ppn = null,
                profileId = pembeliProfiles[0].id,
            ),
            invoiceItem = listOf(
                InvoiceItemEntity(
                    id = 0,
                    invoiceId = 0,
                    price = 50000,
                    productId = products[0].id,
                    unitType = UnitType.CARTON,
                    quantity = 4,
                )
            )
        )
    )
}

fun getNextMonthInvoices(timeService: ITimeService) : List<InvoiceTestData> {
    return listOf(
        InvoiceTestData(
            invoice = InvoiceEntity(
                date = timeService.nextMonth.toNextDay(1, timeService),
                id = 7,
                ppn = 11,
                profileId = supplierProfiles[0].id,
                transactionType = TransactionType.PEMBELIAN
            ),
            invoiceItem = listOf(
                InvoiceItemEntity(
                    id = 0,
                    invoiceId = 0,
                    price = 23000,
                    productId = products[1].id,
                    quantity = 5,
                    unitType = UnitType.PIECE,
                )
            )
        ),

        InvoiceTestData(
            invoice = InvoiceEntity(
                date = timeService.nextMonth.toNextDay(-1, timeService),
                id = 8,
                ppn = null,
                profileId = pembeliProfiles[1].id,
                transactionType = TransactionType.PENJUALAN,
            ),
            invoiceItem = listOf(
                InvoiceItemEntity(
                    id = 0,
                    invoiceId = 0,
                    price = 15000,
                    productId = products[1].id,
                    quantity = 4,
                    unitType = UnitType.CARTON,
                ),
                InvoiceItemEntity(
                    id = 0,
                    invoiceId = 0,
                    price = 12000,
                    productId = products[0].id,
                    quantity = 15,
                    unitType = UnitType.PIECE,
                ),
            )
        )
    )
}

