package com.hezapp.ekonomis.robot

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isEditable
import androidx.compose.ui.test.isFocusable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performFirstLinkClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.hezapp.ekonomis.MainActivity
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.TestConstant
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.getTransactionStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.test_data.TestTimeService
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class TransactionFormRobot(
    private val composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    val context by lazy { composeRule.activity }
    private lateinit var choosenTransactionType : TransactionType
    val chooseProfileString : String
        get() = context.getString(choosenTransactionType.getProfileStringId())

    fun chooseTransactionType(type: TransactionType){
        choosenTransactionType = type

        composeRule.onNode(
            hasText(context.getString(
                R.string.choose_transaction_type_label)))
            .performClick()
        composeRule.onNodeWithText(
            context.getString(R.string.purchase_product_label))
            .performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    fun chooseTransactionDate(day: Int, month: Int, year: Int){
        composeRule.apply {
            onNode(
                hasText(context.getString(
                    R.string.transaction_date_title_label)) and
                        isFocusable())
                .performClick()

            waitUntilExactlyOneExists(isDialog(), timeoutMillis = 5000L)

            // Select year
            onNodeWithContentDescription(
                label = "Switch to selecting a year",
                substring = true
            ).performClick()

            waitUntilAtLeastOneExists(hasText("Navigate to year", substring = true))

            onNode(hasText("Navigate to year $year")).performClick()

            // Select month
            var currentMonth = 2
            while (currentMonth > month){
                onNodeWithContentDescription("Change to previous month")
                    .performClick()
                currentMonth--
            }
            while(currentMonth < month){
                onNodeWithContentDescription("Change to next month")
                    .performClick()
                currentMonth++
            }

            // Select day
            val formatter = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).apply {
                timeZone = TestTimeService.get().getTimezone()
            }
            val targetCalendar = Calendar.getInstance(TestTimeService.get().getTimezone()).apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
            }
            val targetCalendarString = formatter.format(targetCalendar.time)
            onNodeWithText(targetCalendarString).performClick()

            onNodeWithText(context.getString(R.string.choose_label), ignoreCase = true)
                .performClick()
        }
    }

    fun navigateToChooseStakeholder() {
        val fieldLabel = context.getString(
            when(choosenTransactionType){
                TransactionType.PENJUALAN -> R.string.buyer_name_label
                TransactionType.PEMBELIAN -> R.string.seller_name_label
            }
        )
        composeRule.onNodeWithText(fieldLabel).performClick()
    }

    val chooseStakeholderRobot = ChooseStakeholderRobot()

    inner class ChooseStakeholderRobot {
        @OptIn(ExperimentalTestApi::class)
        fun registerNewProfile(profileName: String){
            composeRule.apply {
                waitUntilExactlyOneExists(hasText(
                    context.getString(R.string.create_new_here_label),
                    substring = true
                ), timeoutMillis = TestConstant.MEDIUM_TIMEOUT)

                onNodeWithText(
                    context.getString(R.string.create_new_here_label),
                    substring = true).performFirstLinkClick()

                onNode(
                    hasText(
                        context.getString(
                            R.string.profile_name_label,
                            chooseProfileString.lowercase()
                        ),
                        ignoreCase = true
                    ) and
                    isFocusable()
                ).performTextInput(profileName)

                onNodeWithText(
                    context.getString(R.string.save_profile_label))
                    .performClick()
            }
        }

        @OptIn(ExperimentalTestApi::class)
        fun chooseProfile(profileName: String){
            composeRule.apply {
                waitUntilAtLeastOneExists(hasText(profileName), timeoutMillis = TestConstant.MEDIUM_TIMEOUT)

                onNodeWithText(profileName).performClick()
            }
        }
    }


    fun navigateToChooseProduct(){
        composeRule.onNodeWithText(context.getString(R.string.select_product_label))
            .performClick()
    }

    val chooseProductRobot = ChooseProductRobot()

    inner class ChooseProductRobot {
        private fun search(searchQuery: String){
            composeRule.onNode(
                hasText(context.getString(R.string.search_product_name_label)))
                .performTextInput(searchQuery)
        }

        private fun fillProductName(productName: String){
            composeRule.onNodeWithText(context.getString(R.string.new_product_name_label))
                .performTextInput(productName)
        }

        private fun confirmRegisterNewProduct(){
            composeRule.onNodeWithText(context.getString(R.string.save_label))
                .performClick()
        }

        private fun clickAddNewProduct(){
            composeRule.onNode(
                hasText(
                    context.getString(R.string.register_new_product_here_label),
                    substring = true)
            ).performFirstLinkClick()
        }

        private fun clickProductWithName(productName: String){
            composeRule.onNode(
                hasText(productName) and
                !isEditable()
            ).performClick()
        }

        fun registerNewProduct(newProductName: String){
            search(newProductName)

            clickAddNewProduct()

            confirmRegisterNewProduct()
        }

        private fun specifyUnitType(unitType: UnitType){
            composeRule.onNodeWithText(context.getString(R.string.unit_label))
                .performClick()
            composeRule.onNodeWithText(context.getString(unitType.getStringId()))
                .performClick()
        }

        private fun specifyQuantity(quantity: Int){
            composeRule.onNodeWithText(context.getString(R.string.quantity_label))
                .performTextInput(quantity.toString())
        }

        private fun specifyPrice(price: Int){
            composeRule.onNodeWithText(context.getString(R.string.total_price_label))
                .performTextInput(price.toString())
        }

        private fun confirmChoosenProductSpecification(){
            composeRule.onNodeWithText(context.getString(R.string.choose_label))
                .performClick()
        }

        fun chooseProductForTransaction(
            productName: String,
            quantity: Int,
            unitType: UnitType,
            totalPrice: Int,
        ){
            clickProductWithName(productName)

            specifyUnitType(unitType)

            specifyQuantity(quantity)

            specifyPrice(totalPrice)

            confirmChoosenProductSpecification()
        }

        fun confirmAllSelectedProducts(totalSelectedProduct : Int){
            composeRule.onNodeWithText(context.getString(
                R.string.confirm_selection_with_total_product_selected,
                totalSelectedProduct
            )).performClick()
        }
    }

    fun fillPpn(ppn : Int){
        composeRule.onNodeWithText(context.getString(R.string.ppn_label))
            .performTextInput(ppn.toString())
    }

    fun submitTransactionForm(){
        composeRule.onNodeWithText(context.getString(R.string.save_label))
            .performClick()
    }

    fun assertFormContent(
        transactionType: TransactionType,
        date: LocalDate,
        profileName: String,
        ppn: Int? = null,
        products: List<ProductFormAssertData>,){
        composeRule.apply {
            onNode(
                hasText(context.getString(
                    R.string.choose_transaction_type_label)
                ) and
                hasText(context.getString(
                    transactionType.getTransactionStringId()
                ))
            ).assertExists()

            onNode(
                hasText(
                    context.getString(R.string.transaction_date_title_label)) and
                isFocusable() and
                hasText(
                    TestTimeService.get().toEddMMMyyyy(TestTimeService.get().getCalendar().apply {
                        set(Calendar.MONTH, date.monthValue - 1)
                        set(Calendar.YEAR, date.year)
                        set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                    }.timeInMillis)
                )
            ).assertExists()

            onNode(
                hasText(
                    context.getString(
                        when(transactionType){
                            TransactionType.PENJUALAN -> R.string.buyer_name_label
                            TransactionType.PEMBELIAN -> R.string.seller_name_label
                        }
                    )
                ) and
                hasText(profileName)
            ).assertExists()

            if (transactionType == TransactionType.PENJUALAN)
                onNodeWithText(context.getString(R.string.ppn_label))
                    .assertDoesNotExist()
            else {
                if (ppn == null)
                    throw IllegalStateException(
                        "PPN tidak boleh null ketika tipe transaksi adalah pembelian")
                onNode(
                    hasText(context.getString(R.string.ppn_label)) and
                            hasText("${ppn}%")
                ).assertExists()
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
}

data class ProductFormAssertData(
    val name: String,
    val price: Int,
    val quantity: Int,
    val unitType: UnitType,
)