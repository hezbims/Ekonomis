package com.hezapp.ekonomis.robot.transaction_form

import android.content.Context
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEditable
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.robot._interactor.AnnotatedTextInteractor
import com.hezapp.ekonomis.robot._interactor.ComponentInteractor
import com.hezapp.ekonomis.robot._interactor.TextFieldInteractor

class ChooseProductRobot(
    private val composeRule : ComposeTestRule,
    private val context: Context,
) {
    private val searchTextField = TextFieldInteractor(composeRule, context.getString(R.string.search_product_name_label))
    private val addNewProductLink = AnnotatedTextInteractor(composeRule, hasText(context.getString(R.string.register_new_product_here_label), substring = true))
    private val confirmNewProductRegistrationButton = ComponentInteractor(composeRule, hasText(context.getString(R.string.save_label)))

    private fun clickProductWithName(productName: String){
        composeRule.onNode(
            hasText(productName) and
                    !isEditable()
        ).performClick()
    }

    fun registerNewProduct(newProductName: String){
        searchTextField.inputText(newProductName)
        addNewProductLink.clickLink()
        confirmNewProductRegistrationButton.click()
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
            .performSemanticsAction(SemanticsActions.OnClick)
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