package com.hezapp.ekonomis.edit_product_name_dialog.presentation

import org.junit.Test

class WhenCloseButtonClicked : _BaseEditProductNameDialogUiTest() {

    @Test
    fun `Should dismiss the dialog`(){
        given {
            userOpennedEditProductNameDialog()
        }

        `when` {
            userClickCancelButton()
        }

        then {
            theDialogShouldBeDismissed()
        }
    }
}