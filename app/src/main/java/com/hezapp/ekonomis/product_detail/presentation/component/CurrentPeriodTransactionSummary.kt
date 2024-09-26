package com.hezapp.ekonomis.product_detail.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.invoice_item.entity.UnitType
import com.hezapp.ekonomis.core.domain.product.model.ProductDetail
import com.hezapp.ekonomis.core.presentation.utils.getStringId
import com.hezapp.ekonomis.core.presentation.utils.toRupiah

@Composable
fun LazyItemScope.CurrentPeriodTransactionSummary(productDetail: ProductDetail){
    Text(
        stringResource(R.string.transaction_summary),
        style = MaterialTheme.typography.titleMedium
    )

    SummaryPerTransaction(
        totalPerUnit = productDetail.totalInUnit,
        totalPrice = productDetail.totalInPrice,
        transactionType = TransactionType.PEMBELIAN,
    )

    SummaryPerTransaction(
        totalPerUnit = productDetail.totalOutUnit,
        totalPrice = productDetail.totalOutPrice,
        transactionType = TransactionType.PENJUALAN,
    )
}

@Composable
private fun SummaryPerTransaction(
    totalPerUnit: Map<UnitType, Int>,
    totalPrice: Long,
    transactionType: TransactionType,
){
    val transactionString = when(transactionType){
        TransactionType.PEMBELIAN -> "masuk"
        TransactionType.PENJUALAN -> "keluar"
    }

    Text(
        text = "- Barang $transactionString",
        style = MaterialTheme.typography.bodyMedium,
    )

    Row(modifier = Modifier.padding(start = 20.dp)) {
        Column {
            Text(
                text = "Jumlah barang",
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

@Composable
private fun TotalUnitText(totalPerUnit : Map<UnitType, Int>){
    Text(
        text = "${totalPerUnit[UnitType.CARTON] ?: 0} ${stringResource(UnitType.CARTON.getStringId())}, " +
            "${totalPerUnit[UnitType.PIECE] ?: 0} ${stringResource(UnitType.PIECE.getStringId())}",
        style = MaterialTheme.typography.bodySmall
    )
}