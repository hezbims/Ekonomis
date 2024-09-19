package com.hezapp.ekonomis.add_or_update_transaction.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PercentageVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.isEmpty())
            return TransformedText(text, OffsetMapping.Identity)

        val transformedAnnotatedText = AnnotatedString(
            if (text.isNotEmpty()) "$text%" else ""
        )
        return TransformedText(
            text = transformedAnnotatedText,
            offsetMapping = MyPercentageOffsetMapping(transformedAnnotatedText.length)
        )
    }
}

private class MyPercentageOffsetMapping(private val transformedLength : Int) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        return offset
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset == transformedLength && transformedLength > 0){
            return offset - 1
        }
        return offset
    }
}