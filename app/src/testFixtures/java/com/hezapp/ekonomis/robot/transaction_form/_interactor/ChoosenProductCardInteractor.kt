package com.hezapp.ekonomis.robot.transaction_form._interactor

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.robot._interactor.ComponentInteractor
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.robot._interactor.TextFieldInteractor

class ChoosenProductCardInteractor(
    composeRule: ComposeTestRule,
    matcher: SemanticsMatcher,
    context: Context,
) : ComponentInteractor(composeRule, matcher) {

    private val editIcon = ComponentInteractor(
        composeRule,
        hasParent(matcher) and
                hasContentDescription(context.getString(R.string.edit))
    )
    private val unitTypeDropdown = UnitTypeDropdownInteractor(
        composeRule,
        hasText(context.getString(R.string.unit_label)),
        context
    )
    private val quantityField = TextFieldInteractor(
        composeRule,
        hasText(context.getString(R.string.quantity_label))
    )
    private val totalPriceField = TextFieldInteractor(
        composeRule,
        context.getString(R.string.total_price_label)
    )
    private val deleteIcon = ComponentInteractor(
        composeRule,
        hasContentDescription(context.getString(R.string.delete_label))
    )
    private val confirmButton = ComponentInteractor(
        composeRule,
        hasText(context.getString(R.string.choose_label))
    )

    private val bottomSheetTitleMatcher = hasText(context.getString(R.string.transaction_spesification_title))
    private val bottomSheetMatcher = isDialog() and hasAnyDescendant(bottomSheetTitleMatcher)
    @OptIn(ExperimentalTestApi::class)
    fun performEdit(
        unitType: UnitType?,
        quantity: Int?,
        totalPrice: Int?,
    ){
        editIcon.click()

        unitType?.let {
            unitTypeDropdown.openAndSelectValue(it)
        }

        quantity?.let {
            quantityField.inputText(it.toString(), replaceText = true)
        }

        totalPrice?.let {
            totalPriceField.inputText(it.toString(), replaceText = true)
        }

        confirmButton.click()


        composeRule.waitUntilDoesNotExist(bottomSheetMatcher)
    }

    @OptIn(ExperimentalTestApi::class)
    fun performDelete(){
        editIcon.click()
        deleteIcon.click()

        composeRule.waitUntilDoesNotExist(bottomSheetMatcher)
    }
}