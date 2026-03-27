package com.hezapp.ekonomis.custom_matcher

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText

fun hasRole(expectedRole: Role) =
    SemanticsMatcher.expectValue(SemanticsProperties.Role, expectedRole)

fun hasAccessibleText(text: String, subString: Boolean = false, ignoreCase: Boolean = false) =
    hasText(text = text, substring = subString, ignoreCase = ignoreCase) or
    hasContentDescription(value = text, substring = subString, ignoreCase = ignoreCase)