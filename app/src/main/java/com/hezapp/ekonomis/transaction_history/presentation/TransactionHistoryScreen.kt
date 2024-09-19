package com.hezapp.ekonomis.transaction_history.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.MyScaffold
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.presentation.component.MyBottomNavBar
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.core.presentation.utils.toFullMonthYearString
import com.hezapp.ekonomis.transaction_history.presentation.component.TransactionFilterBottomSheet
import com.hezapp.ekonomis.transaction_history.presentation.component.TransactionHistoryListView

@Composable
fun TransactionHistoryScreen(
    navController : NavHostController,
    viewModel: TransactionHistoryViewModel,
){
    val state = viewModel.state.collectAsStateWithLifecycle().value

    val context = LocalContext.current
    val scaffoldState = remember(state.filterState.monthYear) {
        MyScaffoldState(
            title = {
                Column {
                    Text(context.getString(R.string.transaction_history_title))
                    Text(
                        state.filterState.monthYear.toFullMonthYearString(),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        viewModel.onEvent(TransactionHistoryEvent.ShowFilterBottomSheet)
                    }
                ) {
                    Icon(Icons.Outlined.FilterList, contentDescription = "Filter")
                }
            },
            bottomBar = {
                MyBottomNavBar(
                    currentIndex = 0,
                    navController = navController,
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigateOnce(MyRoutes.AddOrUpdateTransactionForm(null))
                    }
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = context.getString(R.string.add_new_transaction_content_description)
                    )
                }
            }
        )
    }

    MyScaffold(
        scaffoldState = scaffoldState,
        navController = navController,
    ) {
        TransactionHistoryListView(
            navController = navController,
            state = state,
            onEvent = viewModel::onEvent,
        )
    }

    if (state.showFilterBottomSheet)
        TransactionFilterBottomSheet(
            initialState = state.filterState,
            onDismiss = {
                viewModel.onEvent(TransactionHistoryEvent.DismissFilterBottomSheet)
            },
            onConfirmFilter = {
                viewModel.onEvent(TransactionHistoryEvent.ChangeFilter(it))
            }
        )
}