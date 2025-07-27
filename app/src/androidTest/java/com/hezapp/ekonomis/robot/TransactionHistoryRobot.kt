package com.hezapp.ekonomis.robot

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.R

class TransactionHistoryRobot(
    private val composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    private val context by lazy { composeRule.activity }
   fun navigateToProductPreview(){
       composeRule.onNodeWithText(
            context.getString(R.string.product_stock_label),
       ).performClick()
   }

    fun navigateToAddNewTransaction(){
        composeRule.onNodeWithContentDescription(
            context.getString(R.string.add_new_transaction_content_description)
        ).performClick()
    }


}