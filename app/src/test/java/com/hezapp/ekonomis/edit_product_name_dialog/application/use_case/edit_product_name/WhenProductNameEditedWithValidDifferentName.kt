package com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.edit_product_name

import org.junit.Test

class WhenProductNameEditedWithValidDifferentName : _BaseEditProductNameUnitTest() {

    @Test
    fun `Should update the data correctly and return succeed result`() {
        given {
            userHasProductWithNames("Fish", "Meat")
        }

        `when` {
            userEditProduct(withName = "Meat", toName = "Chicken")
        }

        then {
            productsCountInDatabaseShouldBe(2)
            databaseShouldContainsProductWithNames("Fish", "Chicken")
            useCaseResultShouldBeSucceed()
        }
    }
}