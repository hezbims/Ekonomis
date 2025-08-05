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
        transactionType: TransactionType,
        profileName: String,
        date: LocalDate,
        products: List<FormProductItem>,
        ppn: Int? = null,
    ){
        transactionFormRobot.apply {
            chooseTransactionType(transactionType)

            chooseTransactionDate(
                day = date.dayOfMonth,
                month = date.monthValue,
                year = date.year,
            )

            navigateToChooseProfile()
        }

        transactionFormRobot.chooseProfileRobot.apply {
            registerNewProfile(profileName)

            chooseProfile(profileName)
        }

        transactionFormRobot.navigateToChooseProduct()

        transactionFormRobot.chooseProductRobot.apply {
            for (product in products) {
                if (product.newRegistration)
                    registerNewProduct(product.name)

                chooseProductForTransaction(
                    productName = product.name,
                    quantity = product.quantity,
                    unitType = product.unitType,
                    totalPrice = product.totalPrice
                )

                confirmAllSelectedProducts(products.size)
            }
        }

        if (transactionType == TransactionType.PEMBELIAN) {
            transactionFormRobot.fillPpn(ppn ?: throw NullPointerException(
                "PPN harus diisi ketika tipe transaksi adalah pembelian"))
        }

        transactionFormRobot.submitTransactionForm()
    }
}

data class FormProductItem(
    val name: String,
    val quantity: Int,
    val unitType: UnitType,
    val totalPrice: Int,
    val newRegistration : Boolean = false,
)