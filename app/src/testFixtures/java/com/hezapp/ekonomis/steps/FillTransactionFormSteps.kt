package com.hezapp.ekonomis.steps

import android.os.Build
import androidx.annotation.RequiresApi
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
