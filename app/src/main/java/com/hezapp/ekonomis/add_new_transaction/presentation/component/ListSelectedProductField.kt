package com.hezapp.ekonomis.add_new_transaction.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.main_form.AddNewTransactionEvent
import com.hezapp.ekonomis.add_new_transaction.presentation.main_form.AddNewTransactionUiState
import com.hezapp.ekonomis.add_new_transaction.presentation.model.InvoiceItemUiModel
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

/**
 * Menampilkan field untuk barang-barang yang udah dipilih dan tombol untuk menambahkan
 * barang-barang pilihan
 */
@Composable
fun ListSelectedProductField(
    navController: NavHostController,
    state : AddNewTransactionUiState,
    onEvent : (AddNewTransactionEvent) -> Unit,
    modifier : Modifier = Modifier,
){
    Column(
        modifier = modifier.wrapContentHeight(),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "List Barang",
                style = MaterialTheme.typography.titleSmall
            )

            SelectProductButton(navController = navController)
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .defaultMinSize(
                    minHeight = if (state.invoiceItems.isEmpty()) 120.dp
                                else 0.dp
                )
                .fillMaxWidth()
                .border(
                    width = 0.25.dp,
                    shape = MaterialTheme.shapes.small,
                    color = Color.Black
                )
        ) {
            if (state.invoiceItems.isEmpty())
                Text(
                    "Belum ada barang yang anda pilih",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .align(Alignment.Center),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            else
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                ) {
                    state.invoiceItems.forEachIndexed { index , item ->
                        SelectedProductCardItem(item)
                        if (index < state.invoiceItems.lastIndex)
                            HorizontalDivider()
                    }
                }
        }
    }
}

@Composable
fun SelectedProductCardItem(
    item : InvoiceItemUiModel,
    modifier: Modifier = Modifier,
){
    ListItem(
        headlineContent = {
            Text(item.productName)
        },
        supportingContent = {
            Text("${item.quantity} ${stringResource(item.unitType.getStringId())} | ${item.price.toRupiah()}")
        },
        
        trailingContent = {
            IconButton(
                onClick = {}
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = "Edit")
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun SelectProductButton(
    navController : NavHostController
){
    val primaryColor =  MaterialTheme.colorScheme.primary
    
    val stroke = remember {
        Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 2f)
        )
    }

    Box(
        modifier = Modifier
            .drawBehind {
                drawRoundRect(
                    color = primaryColor,
                    style = stroke,
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }
            .clickable { navController.navigateOnce(MyRoutes.SearchAndChooseProduct) }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            Icon(
                Icons.Outlined.Add,
                contentDescription = "Add Icon",
                tint = primaryColor,
                modifier = Modifier
                    .size(16.dp)
                    .border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = primaryColor
                    )
            )

            Text(
                stringResource(R.string.select_product_label),
                color = primaryColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
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
                    state = AddNewTransactionUiState.init(),
                    onEvent = {},
                    modifier = Modifier.fillMaxWidth()
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
                    state = AddNewTransactionUiState
                        .init().copy(
                            invoiceItems = listOf(
                                InvoiceItemUiModel(
                                    productId = 0,
                                    productName = "White Vinegar",
                                    quantity = 500,
                                    unitType = UnitType.PIECE,
                                    id = 0,
                                    price = (1_000_000_000).toInt(),
                                ),
                                InvoiceItemUiModel(
                                    productId = 0,
                                    productName = "Extra Virgin Olive Oil",
                                    quantity = 10,
                                    unitType = UnitType.CARTON,
                                    id = 1,
                                    price = (54_000).toInt(),
                                ),
                            )
                        ),
                    onEvent = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}