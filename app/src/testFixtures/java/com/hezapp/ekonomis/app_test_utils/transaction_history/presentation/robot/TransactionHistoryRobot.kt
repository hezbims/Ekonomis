package com.hezapp.ekonomis.app_test_utils.transaction_history.presentation.robot

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.app_test_utils.core.application.mock.TestTimeService
import com.hezapp.ekonomis.app_test_utils.core.application.mock.testCalendarProvider
import com.hezapp.ekonomis.app_test_utils.core.data.seeder._snapshot.InvoiceSnapshot
import com.hezapp.ekonomis.app_test_utils.transaction_history.presentation.robot.component.FilterTransactionBottomSheetInteractor
import com.hezapp.ekonomis.app_test_utils.transaction_history.presentation.robot.component.TransactionPreviewItemInteractor
import com.hezapp.ekonomis.base_test_utils.robot.component.ComponentInteractor
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.transaction_history.presentation.test_tag.TransactionHistoryScreenTestTag
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * This is robot for [TransactionHistoryScreen][com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryScreen]
 */
class TransactionHistoryRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
    private val components = TransactionHistoryRobotComponents(composeRule, context)
    fun itemWithProfileName(profileName: String) : TransactionPreviewItemInteractor {
        return TransactionPreviewItemInteractor(
            hasText(profileName),
            context,
            composeRule
        )
    }
   fun navigateToProductPreview(){
       composeRule.onNodeWithText(
            context.getString(R.string.product_stock_label),
       ).performClick()
   }

    fun navigateToAddNewTransaction(){
        composeRule.onNodeWithContentDescription(
            context.getString(R.string.add_new_transaction_title)
        ).performClick()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalTestApi::class)
    fun waitAndClickTransactionCard(
        profileName: String,
        pairTotalPriceAndProfileType: Pair<Int, ProfileType>? = null,
        date: LocalDate? = null
    ){
        val dateString = date?.let {
            testCalendarProvider.toEddMMMyyyy(
                it.atStartOfDay(testCalendarProvider.getZoneId())
                    .toInstant()
                    .toEpochMilli()
            )
        }

        var targetSemanticMatcher = hasText(profileName)
        pairTotalPriceAndProfileType?.let {
            if (it.first < 0)
                throw IllegalArgumentException("Total price can't be negative")

            val prefix = when(it.second){
                ProfileType.SUPPLIER -> "-"
                ProfileType.CUSTOMER -> "+"
            }
            val totalPriceString = prefix + it.first.toRupiahV2()

            targetSemanticMatcher = targetSemanticMatcher and hasText(totalPriceString)
        }
        dateString?.let {
            targetSemanticMatcher = targetSemanticMatcher and hasText(it)
        }

        composeRule.waitUntilExactlyOneExists(targetSemanticMatcher)
        composeRule.onNode(targetSemanticMatcher).performClick()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun waitAndClickTransactionCard(
        snapshot: InvoiceSnapshot,
        testTimeService: TestTimeService = TestTimeService.get(),
    ){
        val totalPrice = snapshot.invoiceItems.sumOf { it.price }

        waitAndClickTransactionCard(
            profileName = snapshot.profile.name,
            pairTotalPriceAndProfileType = Pair(totalPrice, snapshot.profile.type),
            date = snapshot.getLocalDate(testTimeService),
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun actionOpenAndApplyFilter(
        targetPeriod: YearMonth? = null,
        isOnlyNotPaidOff: Boolean? = null,
    ){
        components.filterIcon.click()
        components.filterBottomSheet.applyFilter(targetPeriod, isOnlyNotPaidOff)
    }


    @OptIn(ExperimentalTestApi::class)
    fun assertTransactionCardNotExist(
        profileName: String,
    ) {
        composeRule.waitUntilDoesNotExist(hasText(profileName))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun assertTransactionCardExistWith(
        date: LocalDate? = null,
        totalPrice: String? = null,
    ) {
        if (date == null && totalPrice == null){
            throw IllegalArgumentException("At least one parameter is not null")
        }

        if (date != null) {
            val dateFormat = DateTimeFormatter.ofPattern("E, dd-MMM-yyyy")
            composeRule.onNodeWithText(date.format(dateFormat))
                .assertExists()
        }
        if (totalPrice != null){
            composeRule.onNodeWithText(totalPrice)
                .assertExists()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun assertTransactionCardNotExistWith(date: LocalDate){
        val dateFormat = DateTimeFormatter.ofPattern("E, dd-MMM-yyyy")
        composeRule.onNodeWithText(date.format(dateFormat))
            .assertDoesNotExist()
    }

    fun assertTopBarPeriod(expectedDisplay: String) {
        composeRule.onNodeWithTag(TransactionHistoryScreenTestTag.periodTextTopBar)
            .assertTextEquals(expectedDisplay)
    }
}

internal class TransactionHistoryRobotComponents(
    composeRule: ComposeTestRule,
    context: Context,
) {
    val filterIcon = ComponentInteractor(
        composeRule,
        hasContentDescription(context.getString(R.string.open_filter_label)))
    val filterBottomSheet = FilterTransactionBottomSheetInteractor(composeRule,
        isDialog() and
                hasAnyDescendant(hasText(context.getString(R.string.filter_transaction_title))),
        context)

}