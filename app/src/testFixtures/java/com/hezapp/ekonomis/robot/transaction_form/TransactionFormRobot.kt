package com.hezapp.ekonomis.robot.transaction_form

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isFocusable
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.getTransactionStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.robot._interactor.ComponentInteractor
import com.hezapp.ekonomis.robot._interactor.TextFieldInteractor
import com.hezapp.ekonomis.robot.transaction_form._interactor.CalendarPopupInteractor
import com.hezapp.ekonomis.robot.transaction_form._interactor.TransactionTypeDropdownInteractor
import com.hezapp.ekonomis.test_data.TestTimeService
import java.time.LocalDate
import java.util.Calendar

class TransactionFormRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
    private val transactionTypeField = TransactionTypeDropdownInteractor(composeRule, hasText(context.getString(R.string.choose_transaction_type_label)), context)
    private val dateField = ComponentInteractor(composeRule, hasText(context.getString(R.string.transaction_date_label), ignoreCase = true) and isFocusable())
    private val calendarPopup = CalendarPopupInteractor(composeRule, confirmLabel = context.getString(R.string.choose_label))
    private val profileField = ComponentInteractor(
        composeRule,
        hasText(context.getString(R.string.buyer_name_label)) or
        hasText(context.getString(R.string.seller_name_label))
    )
    private val chooseProductButton = ComponentInteractor(composeRule, context.getString(R.string.select_product_label))
    private val ppnField = TextFieldInteractor(composeRule, context.getString(R.string.ppn_label))
    private val submitButton = ComponentInteractor(composeRule, context.getString(R.string.save_label))

    fun chooseTransactionType(type: TransactionType){
        transactionTypeField.openAndSelectTransactionType(type)
    }

    @OptIn(ExperimentalTestApi::class)
    fun chooseTransactionDate(
        day: Int,
        month: Int,
        year: Int,
        expectedCurrentMonth : Int = TestTimeService.get().getCalendar().get(Calendar.MONTH) + 1,
    ){
        composeRule.apply {
            dateField.click()

            waitUntilExactlyOneExists(isDialog(), timeoutMillis = 5000L)

            calendarPopup.changeYear(year)
            calendarPopup.changeMonth(month, expectedCurrentMonth)
            calendarPopup.changeDayOfMonth(day, month, year)
            calendarPopup.confirmDateSelection()
        }
    }

    fun navigateToChooseProfile() : Unit = profileField.click()

    fun navigateToChooseProduct() : Unit = chooseProductButton.click()

    fun fillPpn(ppn : Int) : Unit = ppnField.inputText(ppn.toString())

    fun submitTransactionForm() : Unit = submitButton.click()

    @RequiresApi(Build.VERSION_CODES.O)
    fun assertFormContent(
        transactionType: TransactionType,
        date: LocalDate,
        profileName: String,
        ppn: Int? = null,
        products: List<ProductFormAssertData>,){
        composeRule.apply {
            val dateString = TestTimeService.get().toEddMMMyyyy(TestTimeService.get().getCalendar().apply {
                set(Calendar.MONTH, date.monthValue - 1)
                set(Calendar.YEAR, date.year)
                set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
            }.timeInMillis)

            transactionTypeField.assertHasText(context.getString(
                transactionType.getTransactionStringId()))

            dateField.assertHasText(dateString)

            profileField.assertHasText(profileName)

            if (transactionType == TransactionType.PENJUALAN)
                ppnField.assertDoesNotExist()
            else {
                if (ppn == null)
                    throw IllegalStateException(
                        "PPN tidak boleh null ketika tipe transaksi adalah pembelian")
                ppnField.assertHasText("${ppn}%")
            }

            for (product in products)
                onNode(
                    hasText(product.name, substring = true) and
                    hasText("${product.quantity} " +
                    "${context.getString(product.unitType.getStringId())} | ", substring = true) and
                    hasText(product.price.toRupiahV2(), substring = true)
                ).assertExists()
        }
    }

    val chooseProfileRobot = ChooseProfileRobot(composeRule, context)
    val chooseProductRobot = ChooseProductRobot(composeRule, context)
}

data class ProductFormAssertData(
    val name: String,
    val price: Int,
    val quantity: Int,
    val unitType: UnitType,
)