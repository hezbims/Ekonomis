package com.hezapp.ekonomis.robot.product_detail

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.test_utils.TestConstant

class EditCurrentMonthlyStockDialogRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
    private val cartonLabel = context.getString(R.string.carton_label)
    private val pieceLabel = context.getString(R.string.piece_label)
    private val usePreviousMonthLabel = context.getString(R.string.use_previous_month_calculation)
    private val saveLabel = context.getString(R.string.save_label)
    private val cancelLabel = context.getString(R.string.cancel_label)

    @OptIn(ExperimentalTestApi::class)
    fun waitUntilDataLoaded() {
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText(saveLabel),
            timeoutMillis = TestConstant.SMALL_TIMEOUT,
        )
    }

    fun clickUsePreviousMonthCalculation() {
        composeRule
            .onNodeWithText(usePreviousMonthLabel)
            .performClick()
    }

    fun clickSave() {
        composeRule
            .onNodeWithText(saveLabel)
            .performClick()
    }

    fun clickCancel() {
        composeRule
            .onNodeWithText(cancelLabel)
            .performClick()
    }

    fun assertSaveButtonDisplayed() {
        composeRule
            .onNodeWithText(saveLabel)
            .assertExists()
    }

    fun assertCartonQuantity(expected: Int) {
        composeRule
            .onNodeWithText(expected.toString())
            .assertExists("Expected carton quantity $expected not found")
    }

    fun assertPieceQuantity(expected: Int) {
        composeRule
            .onNodeWithText(expected.toString())
            .assertExists("Expected piece quantity $expected not found")
    }

    fun typeCartonQuantity(value: String) {
        composeRule
            .onNodeWithText(cartonLabel)
            .performTextReplacement(value)
    }

    fun typePieceQuantity(value: String) {
        composeRule
            .onNodeWithText(pieceLabel)
            .performTextReplacement(value)
    }

    @OptIn(ExperimentalTestApi::class)
    fun assertCartonErrorDisplayed() {
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText(context.getString(R.string.cant_be_empty, cartonLabel)),
            timeoutMillis = TestConstant.SMALL_TIMEOUT,
        )
    }
}