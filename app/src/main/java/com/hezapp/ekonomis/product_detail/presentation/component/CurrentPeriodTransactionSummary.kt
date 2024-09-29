package com.hezapp.ekonomis.product_detail.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiah

@Composable
fun CurrentPeriodTransactionSummary(
    productDetail: ProductDetail,
    onClickEditMonthlyStock: () -> Unit,
){
    Text(
        stringResource(R.string.transaction_summary),
        style = MaterialTheme.typography.titleMedium
    )

    Column {
        SummaryPerTransactionType(
            totalPerUnit = productDetail.totalInUnit,
            totalPrice = productDetail.totalInPrice,
            transactionType = TransactionType.PEMBELIAN,
        )

        Spacer(Modifier.height(10.dp))

        SummaryPerTransactionType(
            totalPerUnit = productDetail.totalOutUnit,
            totalPrice = productDetail.totalOutPrice,
            transactionType = TransactionType.PENJUALAN,
        )

        Spacer(Modifier.height(10.dp))

        FirstDayOfMonthStock(
            firstDayOfMonthStock = productDetail.firstDayOfMonthStock,
            onClickEditMontlyStock = onClickEditMonthlyStock
        )

        CurrentStock(currentStock = productDetail.latestDayOfMonthStock)
    }
}

@Composable
private fun SummaryPerTransactionType(
    totalPerUnit: QuantityPerUnitType,
    totalPrice: Long,
    transactionType: TransactionType,
){
    val transactionProductString = when(transactionType){
        TransactionType.PEMBELIAN -> stringResource(R.string.in_product_label)
        TransactionType.PENJUALAN -> stringResource(R.string.out_product_label)
    }

    Column {
        Text(
            text = "\u2022 $transactionProductString",
            style = MaterialTheme.typography.bodyMedium,
        )

        Row(modifier = Modifier.padding(start = 20.dp)) {
            Column {
                Text(
                    text = stringResource(R.string.product_amount_label),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "Total harga",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column {
                for (i in 1..2)
                    Text(" :  ", style = MaterialTheme.typography.bodySmall)
            }

            Column {
                TotalUnitText(totalPerUnit = totalPerUnit)

                Text(
                    text = totalPrice.toRupiah(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun FirstDayOfMonthStock(
    firstDayOfMonthStock: QuantityPerUnitType,
    onClickEditMontlyStock: () -> Unit,
){
    Row(
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            Text(
                text = "\u2022 ${stringResource(R.string.beginning_of_month_stock_label)}",
                style = MaterialTheme.typography.bodyMedium
            )

            TotalUnitText(
                totalPerUnit = firstDayOfMonthStock,
                modifier = Modifier.padding(start = 20.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        TextButton(
            onClick = onClickEditMontlyStock,
            contentPadding = PaddingValues(vertical = 0.dp)
        ) {
            Text(stringResource(R.string.change_label))
        }
    }
}

@Composable
private fun CurrentStock(currentStock: QuantityPerUnitType){
    Column {
        Text(
            text = "\u2022 ${stringResource(R.string.latest_stock_label)}",
            style = MaterialTheme.typography.bodyMedium
        )

        TotalUnitText(
            totalPerUnit = currentStock,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
private fun TotalUnitText(
    totalPerUnit : QuantityPerUnitType,
    modifier : Modifier = Modifier
){
    Text(
        text = "${totalPerUnit.cartonQuantity} ${stringResource(UnitType.CARTON.getStringId())}, " +
            "${totalPerUnit.pieceQuantity} ${stringResource(UnitType.PIECE.getStringId())}",
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier,
    )
}