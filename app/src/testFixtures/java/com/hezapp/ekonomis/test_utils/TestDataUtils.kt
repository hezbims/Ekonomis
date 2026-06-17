package com.hezapp.ekonomis.test_utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.test_utils.db_assertion.MasterDataDbAssertion
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDbAssertion
import com.hezapp.ekonomis.test_utils.seeder.InvoiceSeeder
import com.hezapp.ekonomis.test_utils.seeder.MonthlyStockSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProductSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProfileSeeder
import com.hezapp.ekonomis.test_utils.seeder.dsl.SeederDsl
import org.koin.core.Koin
import org.koin.core.context.GlobalContext

@RequiresApi(Build.VERSION_CODES.O)
class TestDataUtils(
    getKoin: () -> Koin,
) : ITestDataUtils {

    constructor(koin: Koin = GlobalContext.get()) : this(getKoin = {koin})

    //region SEEDER
    override val invoiceSeeder by lazy { InvoiceSeeder(getKoin()) }
    override val productSeeder by lazy { ProductSeeder(getKoin()) }
    override val profileSeeder by lazy { ProfileSeeder(getKoin()) }
    override val monthlyStockSeeder by lazy { MonthlyStockSeeder(getKoin()) }
    //endregion

    //region DSL
    override val seederDsl by lazy { SeederDsl(getKoin()) }
    override val configDsl by lazy { ConfigDsl(getKoin()) }
    //endregion

    //region DB ASSERTION
    override val transactionDbAssertion by lazy { TransactionDbAssertion(getKoin()) }
    override val masterDataDbAssertion by lazy { MasterDataDbAssertion(getKoin()) }
    //endregion
}

interface ITestDataUtils {
    val invoiceSeeder: InvoiceSeeder
    val productSeeder: ProductSeeder
    val profileSeeder: ProfileSeeder
    val monthlyStockSeeder: MonthlyStockSeeder
    val transactionDbAssertion: TransactionDbAssertion
    val masterDataDbAssertion: MasterDataDbAssertion
    val seederDsl: SeederDsl
    val configDsl : ConfigDsl
}