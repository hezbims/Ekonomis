package com.hezapp.ekonomis.add_new_transaction.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RupiahVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedAnnotatedString = text.asRupiah()
        return TransformedText(
            text = transformedAnnotatedString,
            offsetMapping = RupiahOffsetMapping(
                transformedLength = transformedAnnotatedString.length,
                originalLength = text.length,
            )
        )
    }

    private fun AnnotatedString.asRupiah() : AnnotatedString {
        if (isEmpty())
            return AnnotatedString("")
        val result = buildString {
            append("Rp")
            this@asRupiah.forEachIndexed { i, c ->
                append(c)
                if (i + 1 != this@asRupiah.length && ((this@asRupiah.length - i - 1) % 3 == 0))
                    append(".")

            }
        }
        return AnnotatedString(result)
    }


}

private class RupiahOffsetMapping(
    private val transformedLength : Int,
    private val originalLength : Int,
) : OffsetMapping {
    private val totalDot = (originalLength - 1) / 3

    override fun originalToTransformed(offset: Int): Int {
        if (transformedLength == 0) return offset
        // Tidak ada titik sama sekali
        if (originalLength <= 3){
            return offset + 2
        }

        val distanceOffsetToLastOffset = originalLength - offset

        var totalDotAfterThisOffsetInTransformedText = distanceOffsetToLastOffset / 3
        if (distanceOffsetToLastOffset % 3 == 0 && offset == 0){
            --totalDotAfterThisOffsetInTransformedText
        }

        val totalDotBeforeThisOffsetInTransformedText = totalDot - totalDotAfterThisOffsetInTransformedText
        return offset + 2 + totalDotBeforeThisOffsetInTransformedText
    }

    override fun transformedToOriginal(offset: Int): Int {
        // Ngenain tulisan rupiah
        if (offset <= 2){
            return 0
        }
        // Tidak ada titik sama sekali
        if (originalLength <= 3){
            return (offset - 2).coerceAtLeast(0)
        }

        val totalDotAfterOffset = (transformedLength - offset) / 4
        val totalDotBeforeOffset = totalDot - totalDotAfterOffset
        return offset - 2 - totalDotBeforeOffset
    }

}