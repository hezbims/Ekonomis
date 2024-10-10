package com.hezapp.ekonomis.core.presentation.preview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hezapp.ekonomis.MainApplication
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import org.koin.compose.KoinApplication
import org.koin.mp.KoinPlatformTools

@Composable
fun PreviewKoin(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
){
    if (KoinPlatformTools.defaultContext().getOrNull() == null) {
        KoinApplication(
            application = {
                modules(MainApplication.koinModules)
            }
        ) {
            EkonomisTheme {
                Surface(
                    modifier = modifier,
                    content = content
                )
            }
        }
    }
    else {
        EkonomisTheme {
            Surface {
                content()
            }
        }
    }
}