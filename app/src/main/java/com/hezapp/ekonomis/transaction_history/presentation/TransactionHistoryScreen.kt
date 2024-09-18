package com.hezapp.ekonomis.transaction_history.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.presentation.component.MyBottomNavBar
import com.hezapp.ekonomis.core.presentation.model.MyAppBarState
import com.hezapp.ekonomis.transaction_history.presentation.component.TransactionHistoryListView

@Composable
fun TransactionHistoryScreen(
    navController : NavHostController,
    viewModel: TransactionHistoryViewModel,
    onNewAppBarState: (MyAppBarState) -> Unit,
){
    val state = viewModel.state.collectAsState().value

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        onNewAppBarState(
            MyAppBarState(
                bottomBar = {
                    MyBottomNavBar(
                        currentIndex = 0,
                        navController = navController,
                    )
                }
            ).withTitleText(
                context.getString(R.string.transaction_history_title)
            )
        )
    }

    TransactionHistoryListView(
        navController = navController,
        state = state,
        onEvent = viewModel::onEvent,
    )
}