package com.hezapp.ekonomis.transaction_history.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import com.hezapp.ekonomis.transaction_history.domain.model.PreviewTransactionHistory
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun TransactionHistoryCardItem(
    data : PreviewTransactionHistory,
    onClick : () -> Unit,
    modifier : Modifier = Modifier,
){
    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background( color =
                        when(data.personType){
                            ProfileType.SUPPLIER -> Color(0xFFFB8C00)
                            ProfileType.CUSTOMER -> Color(0xFF7CB342)
                        },
                        shape = CircleShape
                    ),
            )
        },
        tonalElevation = 0.75.dp,
        trailingContent = {
            val symbol =
                if (data.personType == ProfileType.SUPPLIER) "-"
                else "+"
            Text(
                "$symbol${data.totalHarga.toString().toRupiah()}"
            )
        },
        overlineContent = {
            Text(data.date)
        },
        headlineContent = {
            Text(data.personName)
        },
        modifier = modifier.clickable { onClick() },
    )
}

@Preview
@Composable
fun PreviewTransactionHistoryCardItem(){
    EkonomisTheme {
        Surface {
            TransactionHistoryCardItem(
                data = PreviewTransactionHistory(
                    personName = "Cik Feni",
                    date = "1-Jan-2023",
                    personType = ProfileType.CUSTOMER,
                    id = 1,
                    totalHarga = 1_000_000_000
                ),
                onClick = {},

            )
        }
    }
}