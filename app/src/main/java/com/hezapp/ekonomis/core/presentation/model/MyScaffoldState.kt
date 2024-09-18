package com.hezapp.ekonomis.core.presentation.model

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class MyScaffoldState(
    val title: @Composable (() -> Unit) = {},
    val bottomBar: @Composable (() -> Unit) = {},
    val navigationIcon: @Composable (() -> Unit)? = null,
    val floatingActionButton: @Composable (() -> Unit) = {},
){
    fun withTitleText(text: String) =
        copy(title = { Text(text) })

}
