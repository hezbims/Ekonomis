package com.hezapp.ekonomis.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hezapp.ekonomis.core.domain.invoice.entity.InvoiceEntity
import com.hezapp.ekonomis.core.domain.invoice_item.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.product.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity

@Database(
    entities = [
        ProductEntity::class,
        ProfileEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class EkonomisDatabase : RoomDatabase() {

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