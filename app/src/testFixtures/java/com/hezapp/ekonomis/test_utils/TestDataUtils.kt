package com.hezapp.ekonomis.test_utils

import com.hezapp.ekonomis.test_utils.db_assertion.MasterDataDbAssertion
import com.hezapp.ekonomis.test_utils.db_assertion.TransactionDbAssertion
import com.hezapp.ekonomis.test_utils.seeder.InvoiceSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProductSeeder
import com.hezapp.ekonomis.test_utils.seeder.ProfileSeeder
import org.koin.core.Koin
import org.koin.core.context.GlobalContext

class TestDataUtils(
    koin: Koin = GlobalContext.get(),
) : ITestDataUtils {
    //region SEEDER
    override val invoiceSeeder = InvoiceSeeder(koin)
    override val productSeeder = ProductSeeder(koin)
    override val profileSeeder = ProfileSeeder(koin)
    //endregion

    //region DB ASSERTION
    override val transactionDbAssertion by lazy { TransactionDbAssertion(koin) }
    override val masterDataDbAssertion by lazy { MasterDataDbAssertion(koin) }
    //endregion
}

interface ITestDataUtils {
    val invoiceSeeder: InvoiceSeeder
    val productSeeder: ProductSeeder
    val profileSeeder: ProfileSeeder
    val transactionDbAssertion: TransactionDbAssertion
    val masterDataDbAssertion: MasterDataDbAssertion
}