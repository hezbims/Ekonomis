package com.hezapp.ekonomis.product_detail.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.monthly_stock.entity.QuantityPerUnitType
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.domain.product.model.TransactionSummary
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.product_detail.presentation.test_tag.ProductDetailTestTag
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun CurrentPeriodTransactionSummary(
    productDetail: ProductDetail,
    onClickEditMonthlyStock: () -> Unit,
    modifier: Modifier = Modifier,
){
    Column(modifier) {
        Text(
            stringResource(R.string.transaction_summary),
            style = MaterialTheme.typography.titleMedium
        )

        FirstDayOfMonthStock(
            firstDayOfMonthStock = productDetail.firstDayOfMonthStock,
            onClickEditMontlyStock = onClickEditMonthlyStock
        )

        SummaryPerTransactionType(
            totalPerUnit = productDetail.totalInUnit,
            totalPrice = productDetail.totalInPrice,
            transactionType = TransactionType.PEMBELIAN,
        )


        SummaryPerTransactionType(
            totalPerUnit = productDetail.totalOutUnit,
            totalPrice = productDetail.totalOutPrice,
            transactionType = TransactionType.PENJUALAN,
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

    Column(
        Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
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
                repeat(2) {
                    Text(" :  ", style = MaterialTheme.typography.bodySmall)
                }
            }

            Column {
                TotalUnitText(
                    totalPerUnit = totalPerUnit,
                    modifier = Modifier.testTag(
                        when (transactionType) {
                            TransactionType.PEMBELIAN -> ProductDetailTestTag.inQuantity
                            TransactionType.PENJUALAN -> ProductDetailTestTag.outQuantity
                        }
                    )
                )

                Text(
                    text = totalPrice.toRupiahV2(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag(
                        when (transactionType) {
                            TransactionType.PEMBELIAN -> ProductDetailTestTag.totalInPrice
                            TransactionType.PENJUALAN -> ProductDetailTestTag.totalOutPrice
                        }
                    )
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
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = 8.dp, top = 4.dp, bottom = 4.dp,
        )
    ) {
        Column {
            Text(
                text = "\u2022 ${stringResource(R.string.beginning_of_month_stock_label)}",
                style = MaterialTheme.typography.bodyMedium
            )

            TotalUnitText(
                totalPerUnit = firstDayOfMonthStock,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .testTag(ProductDetailTestTag.beginningOfMonthStock)
            )
        }

        Spacer(Modifier.width(8.dp))

        FilledTonalIconButton(
            onClick = onClickEditMontlyStock,
        ) {
            Icon(
                Icons.Outlined.Edit,
                contentDescription = stringResource(R.string.edit_first_day_of_month_stock)
            )
        }
    }
}

@Composable
private fun CurrentStock(
    currentStock: QuantityPerUnitType,
){

    Column(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(
            text = "\u2022 ${stringResource(R.string.latest_stock_label)}",
            style = MaterialTheme.typography.bodyMedium
        )

        TotalUnitText(
            totalPerUnit = currentStock,
            modifier = Modifier
                .padding(start = 20.dp)
                .testTag(ProductDetailTestTag.latestStock)
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

@Preview
@Composable
private fun PreviewCurrentPeriodTransactionSummary(){
    EkonomisTheme {
        Surface {
            CurrentPeriodTransactionSummary(
                productDetail = ProductDetail(
                    id = 1,
                    productName = "Tuna",
                    transactionSummary = TransactionSummary(
                        outProductTransactions = listOf(),
                        inProductTransactions = listOf(),
                        firstDayOfMonthStock = QuantityPerUnitType(
                            cartonQuantity = 5,
                            pieceQuantity = 7,
                        ),
                        monthlyStockId = 1,
                    )
                ),
                onClickEditMonthlyStock = { },
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}