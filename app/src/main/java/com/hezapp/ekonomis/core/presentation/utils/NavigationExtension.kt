package com.hezapp.ekonomis.core.presentation.utils

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.core.presentation.routing.MyRoutes

fun NavHostController.goBackSafely(){
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED){
        popBackStack()
    }
}

fun NavHostController.navigateOnce(routes: MyRoutes){
    navigate(routes){
        launchSingleTop = true
    }
}