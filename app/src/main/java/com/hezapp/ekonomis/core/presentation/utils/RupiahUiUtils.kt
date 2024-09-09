package com.hezapp.ekonomis.core.presentation.utils

import java.util.Locale

fun Int.toShortRupiah() : String {
    if (this >= 1_000_000_000)
        return String.format(Locale.US, "Rp%.1f Juta", this.toDouble() / 1_000_000_000)
    else if (this < 10000)
        return "Rp${this}"
    return String.format(Locale.US, "Rp%d Ribu", this / 1_000)
}

fun Int.toRupiah() : String = toString().toRupiah()

fun CharSequence.toRupiah() : String {
    if (isEmpty())
        return ""
    val result = buildString {
        append("Rp")
        this@toRupiah.forEachIndexed { i, c ->
            append(c)
            if (i + 1 != this@toRupiah.length && ((this@toRupiah.length - i - 1) % 3 == 0))
                append(".")

        }
    }
    return result
}