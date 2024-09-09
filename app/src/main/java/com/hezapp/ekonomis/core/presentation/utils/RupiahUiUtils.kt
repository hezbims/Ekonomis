package com.hezapp.ekonomis.core.presentation.utils

import java.text.DecimalFormat
import java.util.Locale

fun Int.toShortRupiah() : String {
    if (this >= 1_000_000_000)
        return String.format(Locale.US, "Rp%.1f Juta", this.toDouble() / 1_000_000_000)

    return String.format(Locale.US, "Rp%d Ribu", this / 1_000)
}

fun Int.toRupiah() : String {
    return rupiahFormat.format(this)
}

private val rupiahFormat = DecimalFormat("#.###")