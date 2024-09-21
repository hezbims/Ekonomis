package com.hezapp.ekonomis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionEvent
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionScreen
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.AddOrUpdateTransactionViewModel
import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_product.SearchAndChooseProductScreen
import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_profile.SearchAndChooseProfileScreen
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.goBackSafely
import com.hezapp.ekonomis.core.presentation.utils.navGraphViewModel
import com.hezapp.ekonomis.product_detail.presentation.ProductDetailScreen
import com.hezapp.ekonomis.product_preview.presentation.ProductPreviewScreen
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryEvent
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryScreen
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryViewModel
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EkonomisTheme {
                val navController = rememberNavController()

                NavHost(
                    navController,
                    startDestination = MyRoutes.NavGraph.Transaction,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    navigation<MyRoutes.NavGraph.Transaction>(
                        startDestination = MyRoutes.TransactionHistory
                    ){
                        composable<MyRoutes.TransactionHistory> {
                            val transHistoryViewModel : TransactionHistoryViewModel? =
                                it.navGraphViewModel(
                                    navController = navController,
                                    countParent = 1
                                )

                            transHistoryViewModel?.let { viewModel ->
                                TransactionHistoryScreen(
                                    navController = navController,
                                    viewModel = viewModel,
                                )
                            }
                        }

                        navigation<MyRoutes.NavGraph.AddOrUpdateTransaction>(
                            startDestination = MyRoutes.AddOrUpdateTransactionForm(id = null),
                        ) {
                            composable<MyRoutes.AddOrUpdateTransactionForm> {
                                val transactionHistoryViewModel : TransactionHistoryViewModel? =
                                    it.navGraphViewModel(navController, 2)

                                val transactionId = it.toRoute<MyRoutes.AddOrUpdateTransactionForm>().id
                                val factory = AddOrUpdateTransactionViewModel.Factory(
                                    transactionId,
                                )
                                val addOrUpdateTransactionViewModel : AddOrUpdateTransactionViewModel? =
                                    it.navGraphViewModel(
                                        navController = navController,
                                        countParent = 1,
                                        factory = factory,
                                    )

                                transactionHistoryViewModel?.let { transHisViewModel ->
                                    addOrUpdateTransactionViewModel?.let { addTransViewModel ->
                                        AddOrUpdateTransactionScreen(
                                            navController = navController,
                                            viewModel = addTransViewModel,
                                            onSubmitSucceed = {
                                                navController.goBackSafely()
                                                transHisViewModel.onEvent(
                                                    TransactionHistoryEvent
                                                        .LoadListPreviewTransactionHistory
                                                )
                                            },
                                        )
                                    }
                                }

                            }

                            composable<MyRoutes.SearchAndChooseProduct> {
                                val addOrUpdateTransViewModel : AddOrUpdateTransactionViewModel? =
                                    it.navGraphViewModel(
                                        navController = navController,
                                        countParent = 1,
                                    )

                                addOrUpdateTransViewModel?.let { viewModel ->
                                    SearchAndChooseProductScreen(
                                        addOrUpdateTransactionViewModel = viewModel,
                                        navController = navController,
                                    )
                                }
                            }

                            composable<MyRoutes.SearchAndChooseProfile> {
                                val addOrUpdateTransactionViewModel : AddOrUpdateTransactionViewModel? =
                                    it.navGraphViewModel(
                                        navController = navController,
                                        countParent = 1,
                                    )

                                addOrUpdateTransactionViewModel?.let { viewModel ->
                                    SearchAndChooseProfileScreen(
                                        transactionType = TransactionType.fromId(
                                            it.toRoute<MyRoutes.SearchAndChooseProfile>().transactionTypeId
                                        ),
                                        onSelectProfile = { selectedProfile ->
                                            viewModel.onEvent(
                                                AddOrUpdateTransactionEvent.ChangeProfile(
                                                    selectedProfile
                                                )
                                            )
                                            navController.goBackSafely()
                                        },
                                        navController = navController,
                                    )
                                }
                            }
                        }
                    }

                    composable<MyRoutes.ProductPreview> {
                        ProductPreviewScreen(
                            navController = navController,
                        )
                    }

                    composable<MyRoutes.DetailProduct> {
                        ProductDetailScreen(
                            productId = it.toRoute<MyRoutes.DetailProduct>().productId,
                            navController = navController,
                        )
                    }

                }

            }
        }
    }
}