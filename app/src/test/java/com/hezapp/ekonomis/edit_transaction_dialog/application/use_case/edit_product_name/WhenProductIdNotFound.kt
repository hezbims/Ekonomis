package com.hezapp.ekonomis.edit_transaction_dialog.application.use_case.edit_product_name

import com.hezapp.ekonomis.edit_product_dialog.application.model.EditProductNameError
import org.junit.Test

class WhenProductIdNotFound : _BaseEditProductNameUnitTest() {

    @Test
    fun `Should not update any data and return failed result`() {
        given {
            userHasProductWithNames("Crab")
        }

        `when` {
            userEditProduct(withId = 0, toName = "Fresh Fish")
        }

        then {
            productsCountInDatabaseShouldBe(1)
            databaseShouldContainsProductWithNames("Crab")
            useCaseResultShouldFailWith(EditProductNameError.ProductIdNotFound)
        }
    }
}
