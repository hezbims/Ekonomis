package com.hezapp.ekonomis.edit_product_name_dialog.presentation

import org.junit.Test

class WhenEditingFailed : _BaseEditProductNameDialogUiTest() {

    @Test
    fun `Should display the error message without dismissing the dialog`(){
        given {
            userOpennedEditProductNameDialog()
        }

        `when` {
            editProductName(to = "already-exist-product-name")
        }

        then {
            theDialogShouldDisplayAppropriateErrorMessage()
        }
    }
}