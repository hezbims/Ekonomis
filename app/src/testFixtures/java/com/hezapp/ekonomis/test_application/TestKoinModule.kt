package com.hezapp.ekonomis.test_application

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hezapp.ekonomis.core.data.database.EkonomisDatabase
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis._testing_only.EkonomisTestDatabase
import com.hezapp.ekonomis.core.domain.utils.IErrorReportingService
import com.hezapp.ekonomis.test_utils.FakeErrorReportingService
import com.hezapp.ekonomis.test_utils.TestTimeService
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
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
        single<ITimeService> {
            val timeZone = TimeZone.getTimeZone("GMT+8")
            TestTimeService(
                currentTimeInMillis = ZonedDateTime.of(
                    LocalDate.of(2020, 2, 15).atStartOfDay(),
                    timeZone.toZoneId(),
                ).toInstant().toEpochMilli(),
                timeZone = timeZone,
                locale = Locale.forLanguageTag("id-ID"),
            )
        }
        single<IErrorReportingService> { FakeErrorReportingService() }
    }
    koin.loadModules(listOf(module), allowOverride = true)
}