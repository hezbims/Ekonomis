package com.hezapp.ekonomis.robot.transaction_form

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.PaymentType
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.getTransactionStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.dto.InstallmentItemAssertionDto
import com.hezapp.ekonomis.dto.PaymentTypeAssertionDto
import com.hezapp.ekonomis.robot._interactor.BackButtonInteractor
import com.hezapp.ekonomis.robot._interactor.ComponentInteractor
import com.hezapp.ekonomis.robot._interactor.ResizableSwitchInteractor
import com.hezapp.ekonomis.robot._interactor.TextFieldInteractor
import com.hezapp.ekonomis.robot.transaction_form._interactor.*
import com.hezapp.ekonomis.test_utils.TestTimeService
import com.hezapp.ekonomis.test_utils.seeder.snapshot.InvoiceSnapshot
import java.time.LocalDate
import java.util.*

class TransactionFormRobot(
    private val composeRule: ComposeTestRule,
    private val context: Context,
) {
    val formTransactionType : TransactionType?
        get(){
            val inputText = composeRule.onNode(transactionTypeField.matcher)
                .fetchSemanticsNode()
                .config[SemanticsProperties.InputText]
                .toString()
            if (inputText.isEmpty())
                return null

            for (transactionType in TransactionType.entries)
                if (context.getString(transactionType.getTransactionStringId()) ==
                    inputText)
                    return transactionType
            throw RuntimeException("Transaction type value in this form is not recognized")
        }
    //region Component Interactor
    private val backButton = BackButtonInteractor(composeRule, context)
    private val transactionTypeField = TransactionTypeDropdownInteractor(composeRule, hasText(context.getString(R.string.choose_transaction_type_label)), context)
    private val dateField = DateFieldInteractor(
        matcher = hasText(context.getString(R.string.transaction_date_title)),
        composeRule = composeRule,
        context = context,
    )
    private val profileField = ComponentInteractor(
        composeRule,
        hasText(context.getString(R.string.buyer_name_title)) or
        hasText(context.getString(R.string.seller_name_title))
    )
    private val chooseProductButton = ComponentInteractor(composeRule, context.getString(R.string.select_product_label))
    private val ppnField = TextFieldInteractor(composeRule, context.getString(R.string.ppn_label))
    private val submitButton = ComponentInteractor(composeRule, context.getString(R.string.save_label))
    private val deleteTransactionIcon = ComponentInteractor(composeRule, hasContentDescription(context.getString(R.string.delete_transaction_label)))
    private fun installmentItemAtIndex(index: Int) : InstallmentListItemInteractor =
        InstallmentListItemInteractor(index, composeRule, context)
    private val installmentItemBottomSheet = InstallmentItemFormInteractor(
        composeRule,
        context
    )
    private val paidOffSwitch = ResizableSwitchInteractor(
        composeRule = composeRule,
        matcher = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch),
        context = context,
    )
    private val addNewInstallmentItemButton = ComponentInteractor(composeRule, hasText(context.getString(R.string.add_new_installment_title)))
    private val paymentTypeRadioGroup = PaymentTypeRadioGroupInteractor(composeRule, context)
    private val paymentMediaGroup = PaymentMediaGroupInteractor(composeRule, context)
    @OptIn(ExperimentalTestApi::class)
    private val confirmDeleteTransactionDialog = object {
        fun confirmDeletion(){
            composeRule.waitUntilExactlyOneExists(isDialog())
            composeRule.onNodeWithText(context.getString(R.string.yes_label))
                .performClick()
        }
    }
    private fun productCardWithName(productName: String) =
        ChoosenProductCardInteractor(composeRule, hasText(productName), context)

    fun chooseTransactionType(type: TransactionType){
        transactionTypeField.openAndSelectTransactionType(type)
    }
    //endregion

    fun backToPreviousScreen(){
        backButton.click()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalTestApi::class)
    fun chooseTransactionDate(
        day: Int,
        month: Int,
        year: Int,
    ){
        dateField.chooseDate(LocalDate.of(year, month, day))
    }

    fun navigateToChooseProfile() {
        profileField.click(useSemanticsAction = false)
    }

    fun navigateToChooseProduct() {
        chooseProductButton.click()
    }

    fun fillPpn(ppn : Int) {
        ppnField.inputText(ppn.toString(), replaceText = true)
    }

    fun submitTransactionForm() {
        submitButton.click()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun assertFormContent(
        transactionType: TransactionType,
        date: LocalDate,
        profileName: String,
        ppn: Int? = null,
        products: List<ProductFormAssertData>,
        paymentTypeAssertion: PaymentTypeAssertionDto,
    ){
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

            val expectedPaymentType = when(paymentTypeAssertion){
                PaymentTypeAssertionDto.Cash -> PaymentType.CASH
                is PaymentTypeAssertionDto.Installment -> PaymentType.INSTALLMENT
            }

            assertSelectedPaymentType(expectedPaymentType)
            if (paymentTypeAssertion is PaymentTypeAssertionDto.Installment){
                assertInstallmentItemsExist(paymentTypeAssertion.items)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun assertFormContent(
        snapshot: InvoiceSnapshot,
        testTimeService: TestTimeService = TestTimeService.get(),
    ){
        val installment = snapshot.installment
        assertFormContent(
            transactionType = snapshot.transactionType,
            date = snapshot.getLocalDate(testTimeService),
            profileName = snapshot.profile.name,
            ppn = snapshot.ppn,
            products = snapshot.invoiceItems.map {
                ProductFormAssertData(
                    name = it.product.name,
                    price = it.price,
                    quantity = it.quantity,
                    unitType = it.unitType,
                )
            },
            paymentTypeAssertion = when(installment){
                null -> PaymentTypeAssertionDto.Cash
                else-> PaymentTypeAssertionDto.Installment(
                    isPaidOff = installment.isPaidOff,
                    items = installment.items.map { installmentItem ->
                        InstallmentItemAssertionDto(
                            amount = installmentItem.amount,
                            paymentDate = installmentItem.paymentDate,
                            paymentMedia = installmentItem.paymentMedia,
                        )
                    }
                )
            }
        )
    }

    fun changeSelectedPaymentType(paymentType: PaymentType){
        paymentTypeRadioGroup.changeSelectedPaymentType(paymentType)
    }

    fun assertSelectedPaymentType(expectedPaymentType: PaymentType){
        paymentTypeRadioGroup.assertSelectedPaymentType(expectedPaymentType)
    }

    private fun assertInstallmentItemsExist(items: List<InstallmentItemAssertionDto>){
        val scrollNode = composeRule.onNode(hasScrollAction())
        items.forEach { item ->
            scrollNode.performScrollToNode(
                hasText(item.amount.toRupiahV2(), substring = true) and
                hasText(TestTimeService.get().toEddMMMyyyy(item.paymentDate)) and
                hasAnyChild(hasContentDescription(context.getString(R.string.edit_installment_item)))
            )
        }
    }

    fun editProduct(
        productName: String,
        unitType: UnitType?,
        quantity: Int?,
        totalPrice: Int?,
    ){
        productCardWithName(productName).performEdit(
            unitType = unitType,
            quantity = quantity,
            totalPrice = totalPrice
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNewInstallmentItem(date: LocalDate, amount: Int, paymentMedia: PaymentMedia){
        addNewInstallmentItemButton.click()
        installmentItemBottomSheet.specifyAndSubmitInput(date, amount, paymentMedia)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editInstallmentItem(index: Int, date: LocalDate, amount: Int, paymentMedia: PaymentMedia){
        installmentItemAtIndex(index).clickEditIcon()
        installmentItemBottomSheet.specifyAndSubmitInput(date, amount, paymentMedia)
    }

    fun deleteInstallmentItemAt(index: Int){
        installmentItemAtIndex(index).performDelete()
    }

    fun deleteProduct(productName: String){
        productCardWithName(productName).performDelete()
    }

    fun deleteCurrentTransaction() {
        deleteTransactionIcon.click()
        confirmDeleteTransactionDialog.confirmDeletion()
    }

    fun assertIsPaidOff(isPaidOff: Boolean) {
        if (isPaidOff)
            paidOffSwitch.assertIsOn()
        else
            paidOffSwitch.assertIsOff()
    }

    fun confirmUnsafePaymentTypeChange() {
        composeRule.onNodeWithText(context.getString(R.string.yes_label))
            .performClick()
    }

    fun togglePaidOff() {
        paidOffSwitch.click()
    }

    fun confirmPaidOffToggleInDialog() {
        composeRule.onNode(hasText(context.getString(R.string.yes_label)))
            .performClick()
    }

    fun assertSubmitButtonNotExist() {
        submitButton.assertDoesNotExist()
    }

    fun assertEmptyDateErrorExist() {
        dateField.assertHasText(context.getString(R.string.transaction_date_cant_be_empty_error))
    }

    fun assertEmptyProfileErrorExist() {
        profileField.assertHasText(when(formTransactionType){
            TransactionType.PEMBELIAN ->
                context.getString(R.string.seller_cant_be_empty_error)
            TransactionType.PENJUALAN ->
                context.getString(R.string.buyer_cant_be_empty_error)
            null -> throw AssertionError("Profile error pasti tidak ada karena tipe transaksi belum dipilih di form ini")
        })
    }

    fun assertEmptyPpnErrorExist() {
        ppnField.assertHasText(context.getString(R.string.ppn_cant_be_empty))
    }

    fun assertEmptyProductListErrorExist(){
        composeRule.onNodeWithText(context.getString(R.string.product_list_cant_be_empty))
            .assertExists()
    }

    fun changeSelectedPaymentMedia(paymentMedia: PaymentMedia) {
        paymentMediaGroup.selectPaymentMedia(paymentMedia)
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