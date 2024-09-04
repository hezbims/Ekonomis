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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hezapp.ekonomis.add_new_transaction.presentation.AddNewTransactionScreen
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
                        composable<MyRoutes.TransactionHistory> {
                            TransactionHistoryScreen(navController)
                        }

                        composable<MyRoutes.ProductPreview> {
                            ProductPreviewScreen()
                        }

                        composable<MyRoutes.AddOrUpdateNewTransaction> {
                            AddNewTransactionScreen(navController)
                        }
                    }
                }
            }
        }
    }
}