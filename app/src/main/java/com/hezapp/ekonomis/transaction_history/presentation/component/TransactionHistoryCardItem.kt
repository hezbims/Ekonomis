package com.hezapp.ekonomis.transaction_history.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.transaction_history.domain.model.PreviewTransactionHistory
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun TransactionHistoryCardItem(
    data : PreviewTransactionHistory,
    onClick : () -> Unit,
    modifier : Modifier = Modifier,
){
    Card(
        modifier = modifier.clickable {
            onClick()
        },
    ) {
        Column(
            modifier = Modifier.padding(
                bottom = 12.dp
            )
        ) {
            Spacer(
                modifier = Modifier
                    .height(12.dp)
                    .fillMaxWidth()
                    .background(
                        if (data.personType == ProfileType.SUPPLIER)
                            Color(0xFFFB8C00)
                        else
                            Color(0xFF7CB342)
                    )
            )

            Column(
                modifier = Modifier.padding(
                start = 24.dp, top = 4.dp
                )
            ) {
                Text(
                    data.personName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    data.date,
                )
            }


        }
    }
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
                ),
                onClick = {},

            )
        }
    }
}