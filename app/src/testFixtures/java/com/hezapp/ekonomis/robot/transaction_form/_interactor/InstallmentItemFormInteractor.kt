package com.hezapp.ekonomis.robot.transaction_form._interactor

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.robot._interactor.ComponentInteractor
import com.hezapp.ekonomis.robot._interactor.TextFieldInteractor
import java.time.LocalDate

class InstallmentItemFormInteractor(
    private val composeRule : ComposeTestRule,
    private val context : Context,
) {
    private val titleMatcher : SemanticsMatcher
        get() = hasText(
            context.getString(R.string.edit_installment_title)) or
                hasText(context.getString(R.string.add_new_installment_title))
    val cancelButtonmMatcher : SemanticsMatcher
        get() = hasText(context.getString(R.string.cancel_label))
    val confirmButtonMatcher : SemanticsMatcher
        get() = hasText(context.getString(R.string.save_label))
    private val bottomSheetMatcher =
        isDialog() and
        hasAnyDescendant(titleMatcher) and
        hasAnyDescendant(cancelButtonmMatcher) and
        hasAnyDescendant(confirmButtonMatcher)

    private val dateField = DateFieldInteractor(
        matcher = hasText(context.getString(R.string.payment_date)) and hasAnyAncestor(bottomSheetMatcher),
        composeRule = composeRule,
        context = context,
    )
    private val amountField = TextFieldInteractor(
        composeRule = composeRule,
        matcher = hasText(context.getString(R.string.payment_amount)) and hasAnyAncestor(bottomSheetMatcher)
    )
    private val submitButton = ComponentInteractor(
        composeRule = composeRule,
        matcher = hasText(context.getString(R.string.save_label)) and hasAnyAncestor(bottomSheetMatcher)
    )
    fun assertDateFieldContent(expectedDate: LocalDate){
        dateField.assertContent(expectedDate)
    }

    fun assertAmountFieldContent(expectedAmount: String){
        amountField.assertHasText(expectedAmount)
    }

    fun assertAmountFieldDoesntHaveError(){
        amountField.assertDoesntHaveText(context.getString(R.string.payment_amount_cant_be_empty))
    }

    fun assertAmountFieldHasError(){
        amountField.assertHasText(context.getString(R.string.payment_amount_cant_be_empty))
    }
    fun submit(){
        submitButton.click()
    }

    fun inputTextInAmountField(text: String, replaceText: Boolean = false) {
        amountField.inputText(text, replaceText = replaceText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectDate(date: LocalDate) {
        dateField.chooseDate(date)
    }

    @OptIn(ExperimentalTestApi::class)
    fun waitUntilAppear(){
        composeRule.waitUntilExactlyOneExists(bottomSheetMatcher)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun specifyAndSubmitInput(date: LocalDate, amount: Int) {
        waitUntilAppear()
        selectDate(date)
        inputTextInAmountField(amount.toString(), replaceText = true)
        submit()
    }
}
