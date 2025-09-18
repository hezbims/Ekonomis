package com.hezapp.ekonomis.core.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OutlinedDropdownField(
    label: String,
    entries : List<T>,
    selectedValue: T,
    onValueChange: (T) -> Unit,
    valueToString : @Composable (T?) -> String,
    isEnabled: Boolean = true,
){
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
                newExpanded ->
            if (isEnabled)
                expanded = newExpanded
        },
    ) {
        OutlinedTextField(
            value = valueToString(selectedValue),
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            enabled = isEnabled,
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            entries.forEach { entry ->
                DropdownMenuItem(
                    text = {
                        Text(valueToString(entry))
                    },
                    onClick = {
                        onValueChange(entry)
                        expanded = false
                    },
                )
            }
        }
    }
}