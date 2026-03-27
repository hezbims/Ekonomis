package com.hezapp.ekonomis.acceptance_test.add_new_transaction

import com.hezapp.ekonomis.test_application.BaseGherkinInstrumentedTest
import org.junit.Test

class AddNewTransactionTest : BaseGherkinInstrumentedTest<AddNewTransactionTestDefinition>(
    createTestDefinition = ::AddNewTransactionTestDefinition
) {
    @Test(timeout = 300_000)
    fun addNewTransactionTest(){
        given.userHasTwoDistinctTransaction()
        `when`.userAddNewUniqueTransaction()
        then.theNewTransactionShouldAdded()
        and.previousTwoTransactionsShouldNotAffected()
    }
}