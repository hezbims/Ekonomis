package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LoadingOverlay(
    isLoading : Boolean,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit,
){
    Box(
        contentAlignment = alignment,
        modifier = modifier
    ) {

        content()

        if (isLoading)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.8f))
            ){
                CircularProgressIndicator()
            }
    }
}