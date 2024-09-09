package com.hezapp.ekonomis

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes
import com.hezapp.ekonomis.core.presentation.utils.goBackSafely

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    navController : NavHostController
){
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination

    TopAppBar(
        navigationIcon = {
            navController.previousBackStackEntry?.let {
                IconButton(
                    onClick = {
                        navController.goBackSafely()
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_icon_content_description)
                    )
                }
            }
        },
        title = {
            if (navBackStackEntry != null) {
                val titleId = MyRoutes.getScreenTitleId(navBackStackEntry)
                Text(stringResource(titleId))
            }
        }
    )
}

