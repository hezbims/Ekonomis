package com.hezapp.ekonomis.core.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.model.MyBasicError
import com.hezapp.ekonomis.core.domain.model.ResponseWrapper
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun <T , E : MyBasicError> ResponseLoader(
    response : ResponseWrapper<T , E>,
    onRetry : () -> Unit,
    modifier: Modifier = Modifier,
    errorToMessage : ((MyBasicError?) -> String)? = null,
    contentAlignment: Alignment = Alignment.Center,
    body : @Composable (T) -> Unit,
){
    Box(
        contentAlignment = contentAlignment,
        modifier = modifier,
    ) {
        when (response) {
            is ResponseWrapper.Failed ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.wrapContentSize(),
                ) {
                    Icon(
                        Icons.Outlined.ErrorOutline,
                        contentDescription = "Error Symbol",
                        modifier = Modifier.size(72.dp)
                    )
                    Text(
                        if (errorToMessage != null)
                            errorToMessage(response.error)
                        else
                            "Terjadi kesalahan tidak diketahui",
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    ElevatedButton(
                        onClick = onRetry
                    ) {
                        Text(stringResource(R.string.retry_label))
                    }
                }
            is ResponseWrapper.Loading ->
                CircularProgressIndicator()
            is ResponseWrapper.Succeed ->
                body(response.data)
        }
    }
}

@Composable
@Preview
fun ResponseLoaderErrorPreview(){
    EkonomisTheme {
        Surface {
            val response : ResponseWrapper<Object, MyBasicError> = ResponseWrapper.Failed()
            ResponseLoader(
                response = response,
                onRetry = {},
            ){}
        }
    }
}