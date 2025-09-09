package com.hezapp.ekonomis.core.presentation.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.ui.theme.EkonomisTheme


@Composable
fun ResizableSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    scale: Double = 1.0,
) {
    val width = (52 * scale).dp
    val height = (32 * scale).dp
    val thumbSize = height - 6.dp
    val offsetX by animateDpAsState(
        targetValue = if (checked) width - thumbSize - 3.dp else 3.dp,
        label = "thumbOffset"
    )

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(CircleShape)
            .background(if (checked) MaterialTheme.colorScheme.primary else Color.LightGray)
            .clickable { onCheckedChange(!checked) }
            .semantics {
                role = Role.Switch
                stateDescription =
                    if (checked) context.getString(R.string.on_label)
                    else context.getString(R.string.off_label)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .size(thumbSize)
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
@Preview
fun PreviewResizableSwitch_Checked(){
    EkonomisTheme {
        Surface(Modifier.padding(24.dp)) {
            ResizableSwitch(
                checked = true,
                onCheckedChange = {},
            )
        }
    }
}

@Composable
@Preview
fun PreviewResizableSwitch_Unchecked(){
    EkonomisTheme {
        Surface(Modifier.padding(24.dp)) {
            ResizableSwitch(
                checked = false,
                onCheckedChange = {},
            )
        }
    }
}