package com.hezapp.ekonomis

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes


@Composable
fun MyBottomNavBar(
    navController: NavHostController
){
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val navbarRoutes = remember { MyRoutes.navigationBarRoutes }

    if (currentDestination?.hasRoute<MyRoutes.TransactionHistory>() == true ||
        currentDestination?.hasRoute< MyRoutes.ProductPreview>() == true) {
        NavigationBar {
            navbarRoutes.forEachIndexed { index, myRoutes ->
                NavigationBarItem(
                    label = {
                        Text(stringResource(myRoutes.labelStringId))
                    },
                    icon = {
                        Icon(
                            myRoutes.icon,
                            contentDescription = stringResource(myRoutes.labelStringId)
                        )
                    },
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        navController.navigate(myRoutes){
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }

}