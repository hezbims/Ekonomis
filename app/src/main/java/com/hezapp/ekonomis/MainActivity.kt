package com.hezapp.ekonomis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.add_new_transaction.presentation.main_form.AddNewTransactionScreen
import com.hezapp.ekonomis.add_new_transaction.presentation.main_form.AddNewTransactionViewModel
import com.hezapp.ekonomis.add_new_transaction.presentation.search_and_choose_product.SearchAndChooseProductScreen
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.product_preview.presentation.ProductPreviewScreen
import com.hezapp.ekonomis.transaction_history.presentation.TransactionHistoryScreen
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EkonomisTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = { MyTopAppBar(navController) },
                    bottomBar = { MyBottomNavBar(navController) },
                    floatingActionButton = { MyFloatingActionButton(navController) },
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = MyRoutes.TransactionHistory,
                        modifier = Modifier.padding(innerPadding),
                        exitTransition = { ExitTransition.None },
                        enterTransition = { EnterTransition.None }
                    ){
                        navigation<MyRoutes.NavGraph.AddOrUpdateTransaction>(
                            startDestination = MyRoutes.AddOrUpdateTransactionForm(id = null),
                        ){
                            composable<MyRoutes.AddOrUpdateTransactionForm> {
                                AddNewTransactionScreen(
                                    navController = navController,
                                    viewModel = getAddNewTransactionViewModel(navController),
                                )
                            }

                            composable<MyRoutes.SearchAndChooseProduct> {
                                SearchAndChooseProductScreen(
                                    navController = navController,
                                    addNewTransactionViewModel = getAddNewTransactionViewModel(navController),
                                )
                            }
                        }

                        composable<MyRoutes.TransactionHistory> {
                            TransactionHistoryScreen(navController)
                        }

                        composable<MyRoutes.ProductPreview> {
                            ProductPreviewScreen()
                        }


                    }
                }
            }
        }
    }

    @Composable
    private fun getAddNewTransactionViewModel(
        navController : NavHostController
    ) : AddNewTransactionViewModel {
        val navGraphBackStackEntry = remember {
            navController.getBackStackEntry(MyRoutes.NavGraph.AddOrUpdateTransaction)
        }
        return viewModel(navGraphBackStackEntry)
    }
}