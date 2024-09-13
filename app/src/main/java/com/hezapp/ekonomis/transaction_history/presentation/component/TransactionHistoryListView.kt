package com.hezapp.ekonomis.transaction_history.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.core.domain.entity.support_enum.ProfileType
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.transaction_history.domain.model.PreviewTransactionHistory
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryEvent
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryUiState
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun TransactionHistoryListView(
    navController : NavHostController,
    state : TransactionHistoryUiState,
    onEvent : (TransactionHistoryEvent) -> Unit,
){
    val navigateToTransactionDetail = state.navigateToTransactionDetail
    LaunchedEffect(navigateToTransactionDetail) {
        if (navigateToTransactionDetail != null){
            onEvent(TransactionHistoryEvent.DoneNavigateToTransactionDetail)

        }
    }

    ResponseLoader(
        response = state.transactionHistoryResponse,
        onRetry = {
            onEvent(TransactionHistoryEvent.LoadListPreviewTransactionHistory)
        },
        modifier = Modifier.fillMaxSize()
    ) { data ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 24.dp, end = 24.dp, bottom = 64.dp, top = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(data){ index, item ->
                TransactionHistoryCardItem(
                    data = item,
                    onClick = {
                        onEvent(TransactionHistoryEvent.NavigateToTransactionDetail(item.id))
                    }
                )

//                HorizontalDivider()
            }
        }
    }
}

@Composable
@Preview
fun PreviewTransactionHistoryListView(){
    val listTransaksi = listOf(
        PreviewTransactionHistory(
            id = 1, date = "13-Jan-2023", personName = "Fajar Milenium", personType = ProfileType.SUPPLIER, totalHarga = 10_000_000,
        ),
        PreviewTransactionHistory(
            id = 2, date = "12-Jan-2023", personName = "Cik Feni", personType = ProfileType.CUSTOMER, totalHarga = 500_000,
        ),
        PreviewTransactionHistory(
            id = 3, date = "11-Jan-2023", personName = "Wiranata", personType = ProfileType.CUSTOMER, totalHarga = 779_000,
        ),
    )
    EkonomisTheme {
        Surface {
            TransactionHistoryListView(
                navController = rememberNavController(),
                onEvent = {_ ->},
                state = TransactionHistoryUiState.init().copy(
                    transactionHistoryResponse = ResponseWrapper.Succeed(
                        data = listTransaksi
                    )
                )
            )
        }
    }
}