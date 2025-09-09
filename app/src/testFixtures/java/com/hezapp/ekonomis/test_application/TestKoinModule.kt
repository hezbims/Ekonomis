package com.hezapp.ekonomis.test_application

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis._testing_only.EkonomisTestDatabase
import com.hezapp.ekonomis.test_utils.TestTimeService
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

fun loadTestKoinModules(
    appContext: Context,
    koin: Koin = GlobalContext.get(),
    useInMemoryDb: Boolean = false,
){
    val module = module {
        val testDb = if (!useInMemoryDb)
                EkonomisTestDatabase.getInstance(appContext)
            else
                Room
                    .inMemoryDatabaseBuilder(appContext, EkonomisTestDatabase::class.java)
                    .addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            db.setForeignKeyConstraintsEnabled(true)
                        }
                    })
                    .build()

        single<EkonomisDatabase> { testDb }
        //region TEST DAO
        single { testDb.productTestDao }
        single { testDb.profileTestDao }
        single { testDb.transactionTestDao }
        single { testDb.installmentTestDao }
        single { testDb.installmentItemTestDao }
        //endregion
        single<ITimeService> { TestTimeService.get() }
    }
    koin.loadModules(listOf(module), allowOverride = true)
}