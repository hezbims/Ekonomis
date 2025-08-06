package com.hezapp.ekonomis.robot.transaction_form._interactor

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.robot._interactor.DropdownInteractor

class UnitTypeDropdownInteractor(
    composeTestRule: ComposeTestRule,
    matcher: SemanticsMatcher,
    private val context: Context,
) : DropdownInteractor(composeTestRule, matcher){
    fun openAndSelectValue(unitType: UnitType){
        val unitTypeString = context.getString(unitType.getStringId())
        openAndSelectValue(unitTypeString)
    }
}