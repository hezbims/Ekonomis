package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation._test_tag.AddOrUpdateTransactionTestTags
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.dto.InstallmentItemUiDto
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.core.presentation.styling.Elevations
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentListItem(
    installmentItem: InstallmentItemUiDto,
    timeService: ITimeService,
    onItemEdited: (newData: InstallmentItemUiDto) -> Unit,
    onItemDeleted: () -> Unit,
){
    var showEditBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(installmentItem.amount.toRupiahV2())
        }, overlineContent = {
            Text(timeService.toEddMMMyyyy(installmentItem.date))
        },
        tonalElevation = Elevations.normal,
        trailingContent = {
            Row {
                IconButton(
                    onClick = {
                        showEditBottomSheet = true
                    }
                ) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit_installment_item)
                    )
                }

                IconButton(
                    onClick = {
                        showDeleteDialog = true
                    },
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = stringResource(R.string.delete_installment_item)
                    )
                }
            }
        },
        modifier = Modifier.testTag(AddOrUpdateTransactionTestTags.installmentListItem),
    )

    InstallmentItemBottomSheetForm(
        visible = showEditBottomSheet,
        onDismissRequest = { showEditBottomSheet = false },
        onSaveData = { onItemEdited(it) },
        timeService = timeService,
        initialData = installmentItem,
    )

    if (showDeleteDialog)
        ConfirmDeleteDialog(
            onDismissDeleteDialog = { showDeleteDialog = false },
            onItemDeleted = onItemDeleted,
        )
}

@Composable
private fun ConfirmDeleteDialog(
    onDismissDeleteDialog: () -> Unit,
    onItemDeleted: () -> Unit,
    properties: DialogProperties = DialogProperties(),
){
    AlertDialog(
        onDismissRequest = onDismissDeleteDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissDeleteDialog()
                    onItemDeleted()
                }
            ) {
                Text(stringResource(R.string.yes_label))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissDeleteDialog
            ) {
                Text(stringResource(R.string.no_label))
            }
        },
        title = {},
        text = {
            Text(stringResource(R.string.are_you_sure_to_delete_this_installment_data_paragraph))
        },
        properties = properties,
    )
}

@Preview
@Composable
private fun PreviewInstallmentListItem(){
    EkonomisTheme {
        Surface {
            InstallmentListItem(
                installmentItem = InstallmentItemUiDto(
                    date = LocalDate.of(2020, 7, 21),
                    amount = 200_000,
                    paymentMedia = PaymentMedia.TRANSFER,
                ),
                timeService = TimeService(),
                onItemEdited = {},
                onItemDeleted = {},
            )
        }
    }
}

@Preview
@Composable
private fun PreviewConfirmDeleteDialog(){
    EkonomisTheme {
        Surface {
            ConfirmDeleteDialog(
                onDismissDeleteDialog = {},
                onItemDeleted = { },
            )
        }
    }
}