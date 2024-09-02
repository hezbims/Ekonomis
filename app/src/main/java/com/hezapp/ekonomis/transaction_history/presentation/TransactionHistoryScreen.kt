package com.hezapp.ekonomis.transaction_history.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.transaction_history.presentation.component.TransactionHistoryListView

@Composable
fun TransactionHistoryScreen(
    navController : NavHostController,
    viewModel : TransactionHistoryViewModel = viewModel(),
){
    val state = viewModel.state.collectAsState().value

    TransactionHistoryListView(
        navController = navController,
        state = state,
        onEvent = viewModel::onEvent,
    )
}