package com.hezapp.ekonomis._testing_only

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.TypeConverters
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.data.invoice.converter.LocalDateConverter
import com.hezapp.ekonomis.core.data.invoice.converter.TransactionTypeConverter
import com.hezapp.ekonomis.core.data.invoice_item.converter.UnitTypeConverter
import com.hezapp.ekonomis.core.data.profile.converter.ProfileTypeConverter
import com.hezapp.ekonomis.core.domain.invoice.entity.Installment
import com.hezapp.ekonomis.core.domain.invoice.entity.InstallmentItem
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis._testing_only.test_dao.InstallmentItemTestDao
import com.hezapp.ekonomis._testing_only.test_dao.InstallmentTestDao
import com.hezapp.ekonomis._testing_only.test_dao.ProductTestDao
import com.hezapp.ekonomis._testing_only.test_dao.ProfileTestDao
import com.hezapp.ekonomis._testing_only.test_dao.TransactionTestDao

@Database(
    entities = [
        ProductEntity::class,
        ProfileEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
        MonthlyStockEntity::class,
        Installment::class,
        InstallmentItem::class,
    ],
    version = 4,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
        ),
        AutoMigration(
            // menambahkan kolom tipe pembayaran di tabel invoice (cash atau cicilan)
            from = 2,
            to = 3,
        ),
        AutoMigration(
            from = 3,
            to = 4,
        )
    ],
    exportSchema = true,
)
@TypeConverters(
    ProfileTypeConverter::class,
    TransactionTypeConverter::class,
    LocalDateConverter::class,
    UnitTypeConverter::class,
)
abstract class EkonomisTestDatabase : EkonomisDatabase() {
    abstract val transactionTestDao : TransactionTestDao
    abstract val productTestDao : ProductTestDao
    abstract val profileTestDao : ProfileTestDao
    abstract val installmentTestDao : InstallmentTestDao
    abstract val installmentItemTestDao : InstallmentItemTestDao

    companion object {
        fun getInstance(context: Context) =
            getInstance(context, EkonomisTestDatabase::class.java)
    }
}