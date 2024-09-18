package com.hezapp.ekonomis

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState

@Composable
fun MyScaffold(
    scaffoldState: MyScaffoldState,
    navController: NavHostController,
    content: @Composable (() -> Unit),
){
    Scaffold(
        topBar = {
            MyTopAppBar(
                scaffoldState = scaffoldState,
                navController = navController,
            )
        },
        bottomBar = { scaffoldState.bottomBar() },
        floatingActionButton = { scaffoldState.floatingActionButton() }
    ) {
        Box(modifier = Modifier.padding(it)){
            content()
        }
    }
}