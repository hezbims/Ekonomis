package com.hezapp.ekonomis.core.presentation.utils

import kotlin.math.abs

@Deprecated(message = "Method ini tidak bisa menghandle negative number", replaceWith = ReplaceWith("toRupiahV2"))
fun Long.toRupiah() : String = toString().toRupiah()
@Deprecated(message = "Method ini tidak bisa menghandle negative number", replaceWith = ReplaceWith("toRupiahV2"))
fun Int.toRupiah() : String = toString().toRupiah()

fun Int.toRupiahV2() : String =
    "${if (this < 0) "-" else ""}${abs(this).toString().toRupiah()}"

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