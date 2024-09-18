package com.hezapp.ekonomis.core.presentation.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.core.presentation.routing.bottomNavBarItems


@Composable
fun MyBottomNavBar(
    currentIndex: Int,
    navController: NavHostController,
){
    NavigationBar {
        bottomNavBarItems.forEachIndexed { index, myRoutes ->
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
                selected = currentIndex == index,
                onClick = {
                    if (index != currentIndex) {
                        navController.navigate(myRoutes) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }

}