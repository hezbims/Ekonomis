package com.hezapp.ekonomis

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
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
import com.hezapp.ekonomis.core.presentation.utils.koinNavGraphViewModel
import com.hezapp.ekonomis.core.presentation.utils.navGraphViewModel
import com.hezapp.ekonomis.product_detail.presentation.EditCurrentMonthlyStockDialog
import com.hezapp.ekonomis.product_detail.presentation.ProductDetailEvent
import com.hezapp.ekonomis.product_detail.presentation.ProductDetailScreen
import com.hezapp.ekonomis.product_detail.presentation.ProductDetailViewModel
import com.hezapp.ekonomis.product_preview.presentation.ProductPreviewScreen
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryEvent
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryScreen
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryViewModel
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import org.koin.compose.KoinIsolatedContext
import org.koin.core.KoinApplication
import org.koin.core.parameter.parametersOf

@Composable
fun MainComposable(koinApp: KoinApplication) {
    val koin = koinApp.koin
    EkonomisTheme {
        val navController = rememberNavController()

        MyKoinNavHost(koinApp, navController){
            navigation<MyRoutes.NavGraph.Transaction>(
                startDestination = MyRoutes.TransactionHistory
            ){
                composable<MyRoutes.TransactionHistory> {
                    val transHistoryViewModel : TransactionHistoryViewModel? =
                        it.koinNavGraphViewModel(
                            navController = navController,
                            countParent = 1
                        )

                    transHistoryViewModel?.let { viewModel ->
                        TransactionHistoryScreen(
                            navController = navController,
                            viewModel = viewModel,
                            timeService = koin.get(),
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
                        val addOrUpdateTransactionViewModel : AddOrUpdateTransactionViewModel? =
                            it.koinNavGraphViewModel(
                                navController = navController,
                                countParent = 1,
                                parameters = { parametersOf(transactionId) },
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
                                    onDeleteSucceed = {
                                        navController.goBackSafely()
                                        transHisViewModel.onEvent(
                                            TransactionHistoryEvent
                                                .LoadListPreviewTransactionHistory
                                        )
                                    },
                                    timeService = koin.get(),
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

            navigation<MyRoutes.NavGraph.ProductDetail>(
                startDestination = MyRoutes.DetailProduct(0)
            ){
                composable<MyRoutes.DetailProduct> { backStack ->
                    val productId = backStack.toRoute<MyRoutes.DetailProduct>().productId
                    val viewModel = backStack.koinNavGraphViewModel<ProductDetailViewModel>(
                        navController = navController,
                        countParent = 1,
                        parameters = { parametersOf(productId) }
                    )

                    viewModel?.let {
                        ProductDetailScreen(
                            productId = productId,
                            navController = navController,
                            viewModel = it,
                        )
                    }
                }

                dialog<MyRoutes.EditMonthlyStock> {
                    val viewModel = it.koinNavGraphViewModel<ProductDetailViewModel>(
                        navController = navController,
                        countParent = 1,
                    )

                    val args = it.toRoute<MyRoutes.EditMonthlyStock>()

                    viewModel?.let { viewModel ->
                        EditCurrentMonthlyStockDialog(
                            args = args,
                            onDismissRequest = {
                                navController.goBackSafely()
                            },
                            onMonthlyStockEdited = {
                                viewModel.onEvent(ProductDetailEvent.LoadDetailProduct)
                                navController.goBackSafely()
                            }
                        )
                    }
                }
            }

            composable<MyRoutes.ProductPreview> {
                ProductPreviewScreen(
                    navController = navController,
                )
            }


        }

    }
}

@Composable
private fun MyKoinNavHost(
    koinApplication: KoinApplication,
    navController: NavHostController,
    builder: NavGraphBuilder.() -> Unit
){
    KoinIsolatedContext(
        koinApplication
    ) {
        NavHost(
            navController,
            startDestination = MyRoutes.NavGraph.Transaction,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            builder = builder,
        )
    }
}