package com.hezapp.ekonomis

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes

@Composable
fun MyFloatingActionButton(
    navController : NavHostController
){
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.hasRoute<MyRoutes.TransactionHistory>() == true){
        FloatingActionButton(
            onClick = {
                navController.navigate(
                    MyRoutes.AddOrUpdateTransactionForm(null)
                ){
                    launchSingleTop = true
                }
            }
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_new_transaction_content_description)
            )
        }
    }
}