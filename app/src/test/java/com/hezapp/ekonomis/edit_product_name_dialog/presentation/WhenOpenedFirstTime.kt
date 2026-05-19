package com.hezapp.ekonomis.edit_product_name_dialog.presentation

import org.junit.Test

class WhenOpenedFirstTime : _BaseEditProductNameDialogUiTest() {
    @Test
    fun `Should display initial product name`(){
        `when` {
            userOpennedEditProductNameDialog()
        }

        then {
            theDialogShouldDisplayInitialEditedTargetProductName()
        }
    }
}