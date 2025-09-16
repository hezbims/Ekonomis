package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation.component.SpecifyProductQuantityAndPriceBottomSheet_EditExistingItem
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionEvent
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionUiState
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.InvoiceItemUiModel
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.styling.BorderWidths
import com.hezapp.ekonomis.core.presentation.styling.Elevations
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

/**
 * Menampilkan field untuk barang-barang yang udah dipilih dan tombol untuk menambahkan
 * barang-barang pilihan
 */
@Composable
fun ListSelectedProductField(
    navController: NavHostController,
    state : AddOrUpdateTransactionUiState,
    onEvent : (AddOrUpdateTransactionEvent) -> Unit,
    error: String?,
    modifier : Modifier = Modifier,
){
    Column(
        modifier
    ) {
        Box{
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .border(
                        width =
                            if (error == null) BorderWidths.normal
                            else BorderWidths.bold,
                        color =
                            if (error == null) MaterialTheme.colorScheme.outline
                            else MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {

                    val total by remember(state.curFormData.invoiceItems) {
                        derivedStateOf {
                            state.curFormData.invoiceItems.sumOf {
                                it.price.toLong()
                            }
                        }
                    }

                    Text(
                        text = "Total : ${total.toRupiahV2()}",
                        style = MaterialTheme.typography.labelSmall
                    )

                    AddNewItemButton(
                        onClick = {
                            navController.navigateOnce(
                                MyRoutes.SearchAndChooseProduct
                            )
                        },
                        label = stringResource(R.string.select_product_label)
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (state.curFormData.invoiceItems.isEmpty())
                    Text(
                        "Belum ada barang yang anda pilih",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        state.curFormData.invoiceItems.forEachIndexed { index, item ->
                            SelectedProductCardItem(
                                item = item,
                                onClickEdit = {
                                    onEvent(
                                        AddOrUpdateTransactionEvent.ChooseInvoiceItemForEdit(
                                            item
                                        )
                                    )
                                }
                            )
                            if (index < state.curFormData.invoiceItems.lastIndex)
                                HorizontalDivider()
                        }
                    }
            }

            Text(
                stringResource(R.string.product_list_label),
                style = MaterialTheme.typography.bodySmall,
                color =
                    if (error != null)
                        MaterialTheme.colorScheme.error
                    else
                        Color.Unspecified,
                fontWeight =
                    if (error != null)
                        FontWeight.Bold
                    else null,
                modifier = Modifier
                    .offset(8.dp, (-8).dp)
                    .padding(horizontal = 4.dp)
                    .background(MaterialTheme.colorScheme.surface)
            )
        }

        error?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }

    state.editInvoiceItem?.let { editItem ->
        SpecifyProductQuantityAndPriceBottomSheet_EditExistingItem(
            invoiceItem = editItem,
            onDismissRequest = {
                onEvent(AddOrUpdateTransactionEvent.CancelEditInvoiceItem)
            },
            onProductSpecificationConfirmed = { newInvoiceItem ->
                onEvent(AddOrUpdateTransactionEvent.EditInvoiceItem(newInvoiceItem))
            },
            onDeleteConfirmed = {
                onEvent(AddOrUpdateTransactionEvent.DeleteInvoiceItem(uuid = editItem.listId))
            }
        )
    }
}

@Composable
fun SelectedProductCardItem(
    item : InvoiceItemUiModel,
    modifier: Modifier = Modifier,
    onClickEdit: () -> Unit,
){
    ListItem(
        headlineContent = {
            Text(item.productName)
        },
        supportingContent = {
            Text("${item.quantity} ${stringResource(item.unitType.getStringId())} | ${item.price.toRupiahV2()}")
        },
        tonalElevation = Elevations.normal,
        trailingContent = {
            IconButton(
                onClick = onClickEdit
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit))
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun PreviewListSelectedProductFieldEmpty(){
    EkonomisTheme {
        Surface {
            Box(
                modifier = Modifier.padding(24.dp)
            ) {
                ListSelectedProductField(
                    navController = rememberNavController(),
                    state = AddOrUpdateTransactionUiState(),
                    onEvent = {},
                    modifier = Modifier.fillMaxWidth(),
                    error = "List barang tidak boleh kosong"
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewListSelectedProductFieldWithItem(){
    EkonomisTheme {
        Surface {
            Box(
                modifier = Modifier.padding(24.dp)
            ) {
                ListSelectedProductField(
                    navController = rememberNavController(),
                    state = AddOrUpdateTransactionUiState().let {
                        val invoiceItems = listOf(
                            InvoiceItemUiModel.new(
                                productId = 0,
                                productName = "White Vinegar",
                                quantity = 500,
                                unitType = UnitType.PIECE,
                                id = 0,
                                price = 1_000_000_000,
                            ),
                            InvoiceItemUiModel.new(
                                productId = 0,
                                productName = "Extra Virgin Olive Oil",
                                quantity = 10,
                                unitType = UnitType.CARTON,
                                id = 1,
                                price = 54_000,
                            ),
                        )
                        it.copy(
                            curFormData = it.curFormData.copy(invoiceItems = invoiceItems),
                        )
                    },
                    onEvent = {},
                    error = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}