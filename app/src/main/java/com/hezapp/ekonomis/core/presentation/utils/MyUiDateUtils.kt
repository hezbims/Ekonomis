package com.hezapp.ekonomis.core.presentation.utils

import android.annotation.SuppressLint
import android.icu.util.Calendar
import java.text.SimpleDateFormat

fun Long.toMyDateString() : String {
    val currentDate = Calendar.getInstance().also { it.timeInMillis = this }
    return myDateFormatter.format(currentDate.time)
}

@SuppressLint("SimpleDateFormat")
private val myDateFormatter = SimpleDateFormat().apply {
    applyPattern("E, dd-MMM-yyyy")
}