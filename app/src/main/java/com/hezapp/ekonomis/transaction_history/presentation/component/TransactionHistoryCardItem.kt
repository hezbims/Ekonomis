package com.hezapp.ekonomis.transaction_history.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.PreviewTimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.core.presentation.preview.PreviewKoin
import com.hezapp.ekonomis.core.presentation.utils.toRupiah
import java.util.Calendar

@Composable
fun TransactionHistoryCardItem(
    data : PreviewTransactionHistory,
    onClick : () -> Unit,
    timeService : ITimeService,
    modifier : Modifier = Modifier,
){
    ListItem(
        leadingContent = {
            if (data.isPaidOff)
                Icon(Icons.Outlined.CheckCircleOutline,
                    contentDescription = stringResource(R.string.paid_off_label)
                )
            else
                Icon(Icons.Outlined.PendingActions,
                    contentDescription = stringResource(R.string.not_paid_off_label))
        },
        tonalElevation = 0.75.dp,
        trailingContent = {
            val symbol : String
            val textColor : Color
            when(data.profileType){
                ProfileType.SUPPLIER -> {
                    textColor = MaterialTheme.colorScheme.error
                    symbol = "-"
                }
                ProfileType.CUSTOMER -> {
                    textColor =
                        if (isSystemInDarkTheme())
                            Color(0xFF81C784)
                        else
                            Color(0xFF388E3C)
                    symbol = "+"
                }
            }

            Text(
                "$symbol${data.totalPrice.toString().toRupiah()}",
                color = textColor
            )
        },
        overlineContent = {
            Text(timeService.toEddMMMyyyy(data.date))
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
    PreviewKoin {
        val date = PreviewTimeService().getCalendar().apply {
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
                totalPrice = 1_000_000_000,
                isPaidOff = false,
            ),
            onClick = {},
            timeService = TimeService()
        )
    }
}