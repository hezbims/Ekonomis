package com.hezapp.ekonomis.edit_product_name_dialog.application.use_case.edit_product_name

import com.hezapp.ekonomis.edit_product_name_dialog.application.model.EditProductNameError
import org.junit.Test

class WhenDifferentProductWithSameNameAlreadyExist : _BaseEditProductNameUnitTest() {

    @Test
    fun `Should not update any data and return failed result`() {
        given {
            userHasProductWithNames("Starfish", "seahorse")
        }

        `when` {
            // can not edit (case-insensitive with trimmed)
            userEditProduct(withName = "seahorse", toName = "\t \tsTaRfish\n   ")
        }

        then {
            productsCountInDatabaseShouldBe(2)
            databaseShouldContainsProductWithNames("Starfish", "seahorse")
            useCaseResultShouldFailWith(EditProductNameError.ProductNameAlreadyExist)
        }
    }
}
