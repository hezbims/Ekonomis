package com.hezapp.ekonomis.core.domain.invoice.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PreviewTransactionFilter(
    val monthYear: Long,
) : Parcelable
