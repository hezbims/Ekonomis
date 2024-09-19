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
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.presentation.utils.toMyDateString
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import java.util.Calendar

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
                        when(data.profileType){
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
                if (data.profileType == ProfileType.SUPPLIER) "-"
                else "+"
            Text(
                "$symbol${data.totalPrice.toString().toRupiah()}"
            )
        },
        overlineContent = {
            Text(data.date.toMyDateString())
        },
        headlineContent = {
            Text(data.profileName)
        },
        modifier = modifier.clickable { onClick() },
    )
}

@Preview
@Composable
fun PreviewTransactionHistoryCardItem(){
    EkonomisTheme {
        Surface {
            val date = Calendar.getInstance().apply {
                set(Calendar.MONTH, 1)
                set(Calendar.YEAR, 2023)
                set(Calendar.DAY_OF_MONTH, 5)
            }
            TransactionHistoryCardItem(
                data = PreviewTransactionHistory(
                    profileName = "Cik Feni",
                    date = date.timeInMillis,
                    profileType = ProfileType.CUSTOMER,
                    id = 1,
                    totalPrice = 1_000_000_000
                ),
                onClick = {},

            )
        }
    }
}