package com.hezapp.ekonomis.app_test_utils.add_or_update_transaction.presentation.robot.component

import android.content.Context
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.base_test_utils.robot.component.DropdownInteractor
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId

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