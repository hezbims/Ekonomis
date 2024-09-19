package com.hezapp.ekonomis.core.domain.invoice.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
data class PreviewTransactionFilter(
    val monthYear: Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH , 1)
        set(Calendar.HOUR_OF_DAY , 0)
        set(Calendar.MINUTE , 0)
        set(Calendar.SECOND , 0)
        set(Calendar.MILLISECOND , 0)
    }.timeInMillis,
) : Parcelable
