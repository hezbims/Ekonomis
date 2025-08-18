package com.hezapp.ekonomis.test_utils.raw_sql_helper

import androidx.sqlite.db.SupportSQLiteDatabase
import com.hezapp.ekonomis.core.data.database.TableNames
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

fun SupportSQLiteDatabase.createNewInvoiceItem(
    productId : Int,
    invoiceId : Int,
    quantity : Int,
    unitType : UnitType,
    totalPrice : Int,
){
    execSQL("""
        INSERT INTO invoice_items(product_id, invoice_id, quantity, price, unit_type)
        VALUES (?, ?, ?, ?, ?)
    """.trimIndent(),
        arrayOf<Any>(productId, invoiceId, quantity, totalPrice, unitType))
}

fun SupportSQLiteDatabase.getProfileById(id: Int) : ProfileEntity {
    val cursor = query("""
        SELECT id, name, type
        FROM profiles
        WHERE id = ?
    """.trimIndent(), arrayOf(id))


    cursor.moveToNext()
    return ProfileEntity(
        id = cursor.getInt(0),
        name = cursor.getString(1),
        type = profileTypeConverter.intToProfileType(cursor.getInt(2))!!
    )
}

fun SupportSQLiteDatabase.createNewInvoice(
    date : LocalDate,
    profileId : Int,
    ppn : Int,
) : Int {
    val dateInMillis = ZonedDateTime.of(
        date.atStartOfDay().plusHours(8),
        ZoneId.of("UTC")
    ).toInstant().toEpochMilli()

    val profile = getProfileById(profileId)
    val transactionTypeId = transactionTypeConverter.transactionTypeToInt(
        profile.type.getTransactionType())!!

    execSQL("""
        INSERT INTO invoices(date, profile_id, ppn, transaction_type)
        VALUES (?, ?, ?, ?)
    """.trimIndent(),
        arrayOf<Any>(dateInMillis, profileId, ppn, transactionTypeId))

    return getLastInsertedId()
}

fun SupportSQLiteDatabase.deleteInvoice(
    id: Int
) : Int {
    val totalDeletedRows = delete(
        TableNames.INVOICE,
        "id = ?",
        arrayOf(id))
    return totalDeletedRows
}

fun SupportSQLiteDatabase.createInstallments(
    installment: CreateInstallmentRawSqlDto,
){
    beginTransaction()
    execSQL("""
        INSERT INTO ${TableNames.INSTALLMENT}(invoice_id, is_paid_off)
        VALUES (?, ?)
    """.trimIndent(),
        arrayOf<Any>(installment.invoiceId, installment.isPaidOff))

    val installmentId = getLastInsertedId()

    for (item in installment.items)
        execSQL("""
        INSERT INTO ${TableNames.INSTALLMENT_ITEMS}(installment_id, payment_date, amount)
        VALUES (?, ?, ?)
    """.trimIndent(),
            arrayOf<Any>(installmentId, item.paymentDate, item.amount))

    setTransactionSuccessful()
    endTransaction()
}

data class CreateInstallmentRawSqlDto(
    val invoiceId : Int,
    val isPaidOff: Boolean,
    val items: List<CreateInstallmentItemRawSqlDto>
)

data class CreateInstallmentItemRawSqlDto(
    val paymentDate: String,
    val amount: Int,
)