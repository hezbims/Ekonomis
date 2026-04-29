package com.hezapp.ekonomis.edit_product_name_dialog.presentation

import org.junit.Test

class WhenEditingSucceed : _BaseEditProductNameDialogUiTest() {
    @Test
    fun `Should close the dialog`(){
        given {
            userOpennedEditProductNameDialog()
        }

        `when` {
            editProductName(to = "valid-fake-product-name")
        }

        then {
            theDialogShouldBeDismissed()
        }
    }
}