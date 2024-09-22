package com.hezapp.ekonomis.product_detail.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.model.ProductTransaction
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toMyDateString
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import java.util.Calendar

@Composable
fun DetailTransactionCardListItem(
    item: ProductTransaction,
    isOutTransaction: Boolean,
    modifier: Modifier = Modifier,
    expandedInitial: Boolean = false,
) {
    var expanded by rememberSaveable { mutableStateOf(expandedInitial) }
    val changeExpanded = { expanded = !expanded }

    OutlinedCard (
        onClick = changeExpanded,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = item.date.toMyDateString(),
                style = MaterialTheme.typography.bodyMedium
            )

            IconButton(
                onClick = changeExpanded,
            ) {
                Icon(
                    imageVector =
                    if (expanded)
                        Icons.Filled.ArrowDropUp
                    else
                        Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                )
            }
        }

        if (expanded)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 26.dp, bottom = 20.dp)
            ) {
                Column {
                    val priceLabel = if (isOutTransaction)
                        stringResource(R.string.selling_price_label)
                    else
                        stringResource(R.string.purchase_price_label)

                    Text(
                        text = stringResource(
                            if (isOutTransaction) R.string.customer_label
                            else R.string.seller_label
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = priceLabel,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Text(
                        text = stringResource(R.string.quantity_label),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    item.ppn?.let {
                        Text(
                            stringResource(R.string.ppn_label),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    Text(
                        if (isOutTransaction)
                            "$priceLabel per ${stringResource(item.unitType.getStringId())}"
                        else
                            "Harga pokok",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Column {
                    for (i in 1..(if (item.ppn == null) 4 else 5))
                        Text(
                            "  :    ",
                            style = MaterialTheme.typography.bodySmall,
                        )
                }

                Column {
                    Text(
                        text = item.profileName,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = item.totalPrice.toRupiah(),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Text(
                        text = "${item.quantity} ${stringResource(item.unitType.getStringId())}",
                        style = MaterialTheme.typography.bodySmall,
                    )

                    item.ppn?.let {
                        Text(
                            text = "$it%",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    Text(
                        text = item.costOfGoodsSold.toRupiah(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

            }

    }
}

@Preview
@Composable
private fun PreviewExpandedOutTransactionCard(){
    EkonomisTheme {
        Surface {
            DetailTransactionCardListItem(
                item = ProductTransaction(
                    id = 0,
                    date = Calendar.getInstance().timeInMillis,
                    ppn = 11,
                    profileName = "Feni",
                    quantity = 5,
                    totalPrice = 2_500_000,
                    unitType = UnitType.PIECE,
                ),
                isOutTransaction = true,
                expandedInitial = true,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}