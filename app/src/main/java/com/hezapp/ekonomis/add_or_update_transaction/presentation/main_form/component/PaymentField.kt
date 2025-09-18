package com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation.main_form.dto.InstallmentItemUiDto
import com.hezapp.ekonomis.add_or_update_transaction.presentation.model.PaymentType
import com.hezapp.ekonomis.core.domain.invoice.entity.PaymentMedia
import com.hezapp.ekonomis.core.domain.utils.ITimeService
import com.hezapp.ekonomis.core.domain.utils.TimeService
import com.hezapp.ekonomis.core.presentation.component.ResizableSwitch
import com.hezapp.ekonomis.core.presentation.styling.BorderWidths
import com.hezapp.ekonomis.core.presentation.utils.stringResource
import com.hezapp.ekonomis.core.presentation.utils.toRupiahV2
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentField(
    selectedPaymentType: PaymentType,
    selectedPaymentMedia: PaymentMedia,
    installmentItems: List<InstallmentItemUiDto>,
    onSelectPaymentType: (PaymentType) -> Unit,
    onSelectPaymentMedia: (PaymentMedia) -> Unit,
    installmentPaidOff: Boolean,
    onChangeInstallmentPaidOff: (Boolean) -> Unit,
    onInstallmentItemAdded: (InstallmentItemUiDto) -> Unit,
    onInstallmentItemEdited: (Int, InstallmentItemUiDto) -> Unit,
    onInstallmentItemDeleted: (Int) -> Unit,
    timeService: ITimeService,
    modifier: Modifier = Modifier,
){
    Box(
        modifier
    ) {
        Column(
            Modifier
                .border(
                    width = BorderWidths.normal,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.type_label),
                style = MaterialTheme.typography.labelMedium,
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .selectableGroup()
            ) {
                listOf(PaymentType.CASH, PaymentType.INSTALLMENT).forEachIndexed { index, paymentType ->
                    Row(
                        Modifier.selectable(
                            selected = selectedPaymentType == paymentType,
                            onClick = {
                                onSelectPaymentType(paymentType)
                            },
                            role = Role.RadioButton,
                        ).weight(1f)
                    ) {
                        RadioButton(
                            selected = selectedPaymentType == paymentType,
                            onClick = null,
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(stringResource(paymentType))
                    }
                }
            }

            if (selectedPaymentType == PaymentType.CASH) {
                Text(
                    stringResource(R.string.media_label),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .selectableGroup()
                ) {
                    PaymentMedia.entries.forEach { paymentMedia ->
                        Row(
                            Modifier.selectable(
                                selected = selectedPaymentMedia == paymentMedia,
                                onClick = {
                                    onSelectPaymentMedia(paymentMedia)
                                },
                                role = Role.RadioButton,
                            ).weight(1f)
                        ) {
                            RadioButton(
                                selected = selectedPaymentMedia == paymentMedia,
                                onClick = null,
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(stringResource(paymentMedia))
                        }
                    }
                }
            }


            if (selectedPaymentType == PaymentType.INSTALLMENT)
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            val totalPrice by remember(installmentItems) {
                                derivedStateOf {
                                    installmentItems.sumOf { it.amount.toLong() }
                                }
                            }
                            Text(
                                "Total : ${totalPrice.toRupiahV2()}",
                                style = MaterialTheme.typography.labelSmall,
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    stringResource(R.string.paid_off_label),
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(Modifier.width(4.dp))

                                ResizableSwitch(
                                    checked = installmentPaidOff,
                                    onCheckedChange = onChangeInstallmentPaidOff,
                                    scale = 0.6,
                                )
                            }
                        }

                        var showAddInstallmentBottomSheet by rememberSaveable { mutableStateOf(false) }
                        Row(
                            horizontalArrangement = Arrangement.End,
                        ) {
                            AddNewItemButton(
                                onClick = {
                                    showAddInstallmentBottomSheet = true
                                },
                                label = stringResource(R.string.add_new_installment_title)
                            )
                        }

                        InstallmentItemBottomSheetForm(
                            visible = showAddInstallmentBottomSheet,
                            onDismissRequest = {
                                showAddInstallmentBottomSheet = false
                            },
                            onSaveData = onInstallmentItemAdded,
                            timeService = timeService,
                        )
                    }

                    if (installmentItems.isNotEmpty())
                        Spacer(Modifier.height(12.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        installmentItems.forEachIndexed { index, it ->
                            InstallmentListItem(
                                installmentItem = it,
                                timeService = timeService,
                                onItemEdited = { newData ->
                                    onInstallmentItemEdited(index, newData)
                                },
                                onItemDeleted = { onInstallmentItemDeleted(index) }
                            )
                        }
                    }

                    if (installmentItems.isEmpty())
                        Text(
                            text = "•  " + stringResource(R.string.no_installment_recorded),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 12.dp)
                        )
                }
            }

        Text(
            stringResource(R.string.payment_label),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .offset(8.dp, (-8).dp)
                .padding(horizontal = 4.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
@Preview
private fun PreviewPaymentField_Installment_With_Item(){
    EkonomisTheme {
        Surface(Modifier.fillMaxWidth()) {
            PaymentField(
                selectedPaymentType = PaymentType.INSTALLMENT,
                installmentItems = listOf(
                    InstallmentItemUiDto(
                        amount = 2_000_000,
                        date = LocalDate.now(),
                        paymentMedia = PaymentMedia.CASH,
                    )
                ),
                onSelectPaymentType = {},
                timeService = TimeService(),
                installmentPaidOff = true,
                onChangeInstallmentPaidOff = {},
                onInstallmentItemDeleted = {},
                onInstallmentItemEdited = { index, it -> },
                onInstallmentItemAdded = {},
                onSelectPaymentMedia = {},
                selectedPaymentMedia = PaymentMedia.CASH,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}

@Composable
@Preview
private fun PreviewPaymentField_Installment_No_Item(){
    EkonomisTheme {
        Surface(Modifier.fillMaxWidth()) {
            PaymentField(
                selectedPaymentType = PaymentType.INSTALLMENT,
                installmentItems = listOf(),
                onSelectPaymentType = {},
                timeService = TimeService(),
                installmentPaidOff = false,
                onChangeInstallmentPaidOff = {},
                onInstallmentItemDeleted = {},
                onInstallmentItemEdited = { index, it -> },
                onInstallmentItemAdded = {},
                onSelectPaymentMedia = {},
                selectedPaymentMedia = PaymentMedia.CASH,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Composable
@Preview
private fun PreviewPaymentField_Cash(){
    EkonomisTheme {
        Surface(Modifier.fillMaxWidth()) {
            PaymentField(
                selectedPaymentType = PaymentType.CASH,
                installmentItems = listOf(
                    InstallmentItemUiDto(
                        amount = 2_000_000,
                        date = LocalDate.now(),
                        paymentMedia = PaymentMedia.TRANSFER,
                    )
                ),
                onSelectPaymentType = {},
                timeService = TimeService(),
                installmentPaidOff = false,
                onChangeInstallmentPaidOff = {},
                onInstallmentItemDeleted = {},
                onInstallmentItemEdited = { index, it -> },
                onInstallmentItemAdded = {},
                selectedPaymentMedia = PaymentMedia.CASH,
                onSelectPaymentMedia = {},
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}