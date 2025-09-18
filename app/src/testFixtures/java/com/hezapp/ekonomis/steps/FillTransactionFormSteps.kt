package com.hezapp.ekonomis.steps

import android.os.Build
import androidx.annotation.RequiresApi
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.PaymentType
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.robot.transaction_form.TransactionFormRobot
import java.time.LocalDate

class FillTransactionFormSteps(
    private val transactionFormRobot: TransactionFormRobot,
) {
    /**
     * Ini hanya bisa digunakan ketika anda sudah berada di halaman form pengisian data transaksi
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun fillForm(
        transactionType: TransactionType? = null,
        profileName: String,
        date: LocalDate,
        chooseProductActions: List<FormProductItem> = emptyList(),
        modifyChoosenProductActions: List<ModifyChoosenProduct> = emptyList(),
        modifyPaymentSectionActions: List<ModifyPaymentSectionAction> = emptyList(),
        ppn: Int? = null,
        isRegisterNewProfile: Boolean = true,
    ){
        transactionFormRobot.apply {
            transactionType?.let {
                chooseTransactionType(it)
            }

            chooseTransactionDate(
                day = date.dayOfMonth,
                month = date.monthValue,
                year = date.year,
            )

            navigateToChooseProfile()
        }

        transactionFormRobot.chooseProfileRobot.apply {
            if (isRegisterNewProfile)
                registerNewProfile(profileName)

            chooseProfile(profileName)
        }

        if (transactionFormRobot.formTransactionType == TransactionType.PEMBELIAN)
            transactionFormRobot.fillPpn(ppn ?: throw IllegalArgumentException(
                "PPN harus diisi ketika tipe transaksi adalah pembelian"))

        if (chooseProductActions.isNotEmpty()) {
            transactionFormRobot.navigateToChooseProduct()

            transactionFormRobot.chooseProductRobot.apply {
                for (product in chooseProductActions) {
                    if (product.newRegistration)
                        registerNewProduct(product.name)

                    chooseProductForTransaction(
                        productName = product.name,
                        quantity = product.quantity,
                        unitType = product.unitType,
                        totalPrice = product.totalPrice
                    )

                    confirmAllSelectedProducts(chooseProductActions.size)
                }
            }
        }

        if (modifyChoosenProductActions.isNotEmpty())
            transactionFormRobot.apply {
                for (action in modifyChoosenProductActions)
                    when (action) {
                        is DeleteProduct ->
                            deleteProduct(productName = action.targetName)
                        is EditProduct ->
                            editProduct(
                                productName = action.targetName,
                                quantity = action.quantity,
                                unitType = action.unitType,
                                totalPrice = action.totalPrice,
                            )
                    }
            }

        for (action in modifyPaymentSectionActions)
            when(action){
                is ModifyPaymentSectionAction.AddNewInstallmentItem ->
                    transactionFormRobot.addNewInstallmentItem(action.date, action.amount, action.paymentMedia)
                is ModifyPaymentSectionAction.EditInstallmentItem ->
                    transactionFormRobot.editInstallmentItem(action.index, action.date, action.amount, action.paymentMedia)
                is ModifyPaymentSectionAction.DeleteInstallmentItem ->
                    transactionFormRobot.deleteInstallmentItemAt(action.index)
                ModifyPaymentSectionAction.SelectOneTimePaymentType ->
                    transactionFormRobot.changeSelectedPaymentType(PaymentType.CASH)
                ModifyPaymentSectionAction.SelectInstallmentPaymentType ->
                    transactionFormRobot.changeSelectedPaymentType(PaymentType.INSTALLMENT)
                is ModifyPaymentSectionAction.SelectPaymentMedia ->
                    transactionFormRobot.changeSelectedPaymentMedia(action.paymentMedia)
            }

        transactionFormRobot.submitTransactionForm()
    }
}

sealed interface ModifyChoosenProduct

data class FormProductItem(
    val name: String,
    val quantity: Int,
    val unitType: UnitType,
    val totalPrice: Int,
    val newRegistration : Boolean = false,
)

data class EditProduct(
    val targetName: String,
    val quantity: Int?,
    val unitType: UnitType?,
    val totalPrice: Int?,
) : ModifyChoosenProduct


data class DeleteProduct(
    val targetName: String,
) : ModifyChoosenProduct

sealed interface ModifyPaymentSectionAction {
//    data object ChangePaymentTypeToCash : ModifyPaymentSectionAction
//    data object ChangePaymentTypeToInstallment : ModifyPaymentSectionAction
    data class AddNewInstallmentItem(
        val amount: Int,
        val date: LocalDate,
        val paymentMedia: PaymentMedia,
    ) : ModifyPaymentSectionAction

    data class EditInstallmentItem(
        val index: Int,
        val amount: Int,
        val date: LocalDate,
        val paymentMedia: PaymentMedia,
    ) : ModifyPaymentSectionAction

    data class DeleteInstallmentItem(val index: Int) : ModifyPaymentSectionAction

    data object SelectInstallmentPaymentType : ModifyPaymentSectionAction
    data object SelectOneTimePaymentType : ModifyPaymentSectionAction
    data class SelectPaymentMedia(val paymentMedia: PaymentMedia) : ModifyPaymentSectionAction
}