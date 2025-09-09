package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.ui.theme.EkonomisTheme

@Composable
fun AddNewItemButton(
    onClick: () -> Unit,
    label: String,
){
    val primaryColor = MaterialTheme.colorScheme.primary
    val stroke = remember {
        Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 2f)
        )
    }

    Box(
        modifier = Modifier
            .drawBehind {
                drawRoundRect(
                    color = primaryColor,
                    style = stroke,
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            Icon(
                Icons.Outlined.Add,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier
                    .size(16.dp)
                    .border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = primaryColor
                    )
            )

            Text(
                label,
                color = primaryColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview
@Composable
private fun PreviewChooseItemButton(){
    EkonomisTheme {
        AddNewItemButton(
            onClick = {},
            label = stringResource(R.string.add_new_installment_title)
        )
    }
}