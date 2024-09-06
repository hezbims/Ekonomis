package com.hezapp.ekonomis.add_new_transaction.presentation.component

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_new_transaction.presentation.AddNewTransactionEvent
import com.hezapp.ekonomis.add_new_transaction.presentation.AddNewTransactionUiState
import com.hezapp.ekonomis.core.domain.entity.InvoiceItemEntity
import com.hezapp.ekonomis.core.domain.entity.ProductEntity
import com.hezapp.ekonomis.core.domain.entity.relationship.InvoiceItemWithProduct
import com.hezapp.ekonomis.core.domain.entity.support_enum.UnitType
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toShortRupiah
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun ListSelectedProductField(
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

            SelectProductButton(
                onClick = {}
            )
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = 120.dp)
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
                    style = MaterialTheme.typography.bodyMedium
                )
            else
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    state.invoiceItems.forEach {
                        SelectedProductCardItem(it)
                    }
                }
        }
    }
}

@Composable
fun SelectedProductCardItem(
    item : InvoiceItemWithProduct,
){
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
        ){
            Box(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                Text(
                    item.product.name,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                "${item.invoiceItem.quantity} ${stringResource(item.invoiceItem.unitType.getStringId())}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                item.invoiceItem.price.toShortRupiah(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun SelectProductButton(
    onClick : () -> Unit,
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
            .clickable{  onClick() }
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
                    state = AddNewTransactionUiState
                        .init().copy(
                            invoiceItems = listOf(
                                InvoiceItemWithProduct(
                                    invoiceItem = InvoiceItemEntity(
                                        productId = 0,
                                        price = (14_000_000_000).toInt(),
                                        quantity = 5,
                                        invoiceId = 1,
                                        unitType = UnitType.PIECE,
                                    ),
                                    product = ProductEntity(
                                        id = 0,
                                        name = "White Vinegar"
                                    ),
                                ),
                                InvoiceItemWithProduct(
                                    invoiceItem = InvoiceItemEntity(
                                        productId = 0,
                                        price = (54_000).toInt(),
                                        quantity = 10,
                                        invoiceId = 1,
                                        unitType = UnitType.CARTON,
                                    ),
                                    product = ProductEntity(
                                        id = 0,
                                        name = "Extra Virgin Olive Oil"
                                    ),
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