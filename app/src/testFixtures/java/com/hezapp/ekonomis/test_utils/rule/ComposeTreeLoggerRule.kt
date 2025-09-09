package com.hezapp.ekonomis.test_utils.rule

import android.util.Log
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.printToLog
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Memprint semantics tree terakhir ketika test gagal. Ordernya harus lebih
 * tinggi dari composeRule
 */
class ComposeTreeLoggerRule(
    private val composeRule: ComposeTestRule,
) : TestWatcher() {
    override fun failed(e: Throwable?, description: Description?) {
        try {
            composeRule.onAllNodes(isRoot()).printToLog(
                tag = "QQQ Merged",
                maxDepth = Int.MAX_VALUE
            )
        } catch (t : Throwable) {
            Log.e("QQQ error", "Error printing semantics tree at the end of test " +
                    (t.message ?: "Unknown error"))
        }
        super.failed(e, description)
    }
}
