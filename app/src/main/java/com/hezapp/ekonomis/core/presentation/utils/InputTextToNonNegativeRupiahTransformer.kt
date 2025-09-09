package com.hezapp.ekonomis.core.presentation.utils


class InputTextToNonNegativeRupiahTransformer {
    operator fun invoke(
        inputText: String,
        defaultValue: Int?,
    ) : Int? {
        if (defaultValue != null && defaultValue < 0)
            throw IllegalArgumentException("Default value can't be negative integer")
        if (inputText.isEmpty())
            return null

        return inputText.toIntOrNull().let { resultInteger ->
            if (resultInteger == null || resultInteger < 0)
                return@let defaultValue
            resultInteger
        }
    }
}