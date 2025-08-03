package com.hezapp.ekonomis.transaction_history.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.MyScaffold
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionFilter
import com.hezapp.ekonomis.core.domain.invoice.model.PreviewTransactionHistory
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.core.presentation.component.MyBottomNavBar
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.navigateOnce
import com.hezapp.ekonomis.transaction_history.presentation.component.TransactionFilterBottomSheet
import com.hezapp.ekonomis.transaction_history.presentation.component.TransactionHistoryListView
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import org.koin.core.context.GlobalContext
import java.util.Calendar

@Composable
fun TransactionHistoryScreen(
    navController : NavHostController,
    viewModel: TransactionHistoryViewModel,
    timeService: ITimeService = GlobalContext.get().get(),
){
    TransactionHistoryScreen(
        navController = navController,
        state = viewModel.state.collectAsStateWithLifecycle().value,
        onEvent = viewModel::onEvent,
        timeService = timeService,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionHistoryScreen(
    navController : NavHostController,
    state: TransactionHistoryUiState,
    onEvent: (TransactionHistoryEvent) -> Unit,
    timeService: ITimeService,
){
    val context = LocalContext.current
    val scaffoldState = remember(state.filterState.monthYear) {
        MyScaffoldState(
            title = {
                Column {
                    Text(context.getString(R.string.transaction_history_title))
                    Text(
                        timeService.toMMMMyyyy(state.filterState.monthYear),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        onEvent(TransactionHistoryEvent.ShowFilterBottomSheet)
                    }
                ) {
                    Icon(Icons.Outlined.FilterList, contentDescription = stringResource(R.string.open_filter_label))
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
            onEvent = onEvent,
            timeService = timeService,
        )
    }

    if (state.showFilterBottomSheet)
        TransactionFilterBottomSheet(
            initialState = state.filterState,
            onDismiss = {
                onEvent(TransactionHistoryEvent.DismissFilterBottomSheet)
            },
            onConfirmFilter = {
                onEvent(TransactionHistoryEvent.ChangeFilter(it))
            },
            timeService = timeService,
        )
}

@Composable
@Preview
private fun TransactionHistoryScreen_Preview(){
    EkonomisTheme {
        Surface {
            TransactionHistoryScreen(
                navController = rememberNavController(),
                state = TransactionHistoryUiState(
                    transactionHistoryResponse = ResponseWrapper.Succeed(
                        listOf(
                            PreviewTransactionHistory(
                                id = 1,
                                profileName = "Metri",
                                profileType = ProfileType.CUSTOMER,
                                date = TimeService().getCalendar().timeInMillis,
                                totalPrice = 25_000_000
                            )
                        )
                    ),
                    filterState = PreviewTransactionFilter(
                        monthYear = TimeService().getCalendar().apply {
                            set(Calendar.DAY_OF_MONTH, 1)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                    )
                ),
                onEvent = {},
                timeService = TimeService(),
            )
        }
    }
}