package com.hezapp.ekonomis.core.presentation.utils

import java.util.Locale

fun Int.toShortRupiah() : String {
    if (this >= 1_000_000_000)
        return String.format(Locale.US, "Rp%.1f Juta", this.toDouble() / 1_000_000_000)

    return String.format(Locale.US, "Rp%d Ribu", this / 1_000)
}