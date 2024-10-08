package com.hezapp.ekonomis.transaction_history.presentation.component

import android.icu.util.Calendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.PreviewCalendarProvider
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryEvent
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryUiState
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun TransactionHistoryListView(
    navController : NavHostController,
    state : TransactionHistoryUiState,
    onEvent : (TransactionHistoryEvent) -> Unit,
    modifier : Modifier = Modifier,
){
    ResponseLoader(
        response = state.transactionHistoryResponse,
        onRetry = {
            onEvent(TransactionHistoryEvent.LoadListPreviewTransactionHistory)
        },
        modifier = modifier.fillMaxSize()
    ) { data ->
        if (data.isNotEmpty())
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 24.dp, end = 24.dp, bottom = 64.dp, top = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(data){ _, item ->
                    TransactionHistoryCardItem(
                        data = item,
                        onClick = {
                            navController.navigateOnce(
                                MyRoutes.AddOrUpdateTransactionForm(id = item.id)
                            )
                        }
                    )
                }
            }
        else
            Text(
                "Anda belum memiliki riwayat transaksi",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
            )
    }
}

@Composable
@Preview
fun PreviewTransactionHistoryListView(){
    val initialDate = PreviewCalendarProvider().getCalendar().apply {
        set(Calendar.MONTH, 1)
        set(Calendar.YEAR , 2023)
    }
    val listDate = List(3){ index ->
        initialDate.let {
            it.set(Calendar.DAY_OF_MONTH, 10 + index)
            it.timeInMillis
        }
    }
    val listTransaksi = listOf(

        PreviewTransactionHistory(
            id = 1, date = listDate[0], profileName = "Fajar Milenium", profileType = ProfileType.SUPPLIER, totalPrice = 10_000_000,
        ),
        PreviewTransactionHistory(
            id = 2, date = listDate[1], profileName = "Cik Feni", profileType = ProfileType.CUSTOMER, totalPrice = 500_000,
        ),
        PreviewTransactionHistory(
            id = 3, date = listDate[2], profileName = "Wiranata", profileType = ProfileType.CUSTOMER, totalPrice = 779_000,
        ),
    )
    EkonomisTheme {
        Surface {
            TransactionHistoryListView(
                navController = rememberNavController(),
                onEvent = {_ ->},
                state = TransactionHistoryUiState().copy(
                    transactionHistoryResponse = ResponseWrapper.Succeed(
                        data = listTransaksi
                    )
                )
            )
        }
    }
}