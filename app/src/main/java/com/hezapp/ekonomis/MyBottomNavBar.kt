package com.hezapp.ekonomis

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes

@Composable
fun MyBottomNavBar(
    navController: NavHostController
){
    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar {
        MyRoutes.navigationBarRoutes.forEachIndexed { index, myRoutes ->
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