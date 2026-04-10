package com.hezapp.ekonomis.acceptance_test.edit_transaction

import com.hezapp.ekonomis.test_application.BaseGherkinInstrumentedTest
import org.junit.Test

class EditTransactionTest : BaseGherkinInstrumentedTest<EditTransactionTestDefinition>(
    createTestDefinition = ::EditTransactionTestDefinition
) {
    @Test(timeout = 300_000)
    fun addNewTransactionTest(){
        given.userHasExistingThreeUniqueTransaction()
        `when`.userEditTheSecondTransaction()
        then.theSecondTransactionShouldEdited()
        and.otherTransactionsIsUnchanged()
    }
}