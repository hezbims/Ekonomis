package com.hezapp.ekonomis.core.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hezapp.ekonomis.core.data.invoice.converter.TransactionTypeConverter
import com.hezapp.ekonomis.core.data.invoice.dao.InvoiceDao
import com.hezapp.ekonomis.core.data.invoice_item.converter.UnitTypeConverter
import com.hezapp.ekonomis.core.data.invoice_item.dao.InvoiceItemDao
import com.hezapp.ekonomis.core.data.monthly_stock.dao.MonthlyStockDao
import com.hezapp.ekonomis.core.data.product.dao.ProductDao
import com.hezapp.ekonomis.core.data.profile.converter.ProfileTypeConverter
import com.hezapp.ekonomis.core.data.profile.dao.ProfileDao
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.MonthlyStockEntity
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

@Database(
    entities = [
        ProductEntity::class,
        ProfileEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
        MonthlyStockEntity::class,
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
        ),
    ],
    exportSchema = true,
)
@TypeConverters(
    ProfileTypeConverter::class,
    TransactionTypeConverter::class,
    UnitTypeConverter::class
)
abstract class EkonomisDatabase : RoomDatabase() {

    abstract val profileDao: ProfileDao
    abstract val productDao: ProductDao
    abstract val invoiceDao: InvoiceDao
    abstract val invoiceItemDao: InvoiceItemDao
    abstract val monthlyStockDao: MonthlyStockDao

    companion object {
        private const val DB_NAME = "ekonomis_db"

        @Volatile
        private var INSTANCE : EkonomisDatabase? = null

        fun getInstance(context: Context) : EkonomisDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        EkonomisDatabase::class.java,
                        DB_NAME
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}