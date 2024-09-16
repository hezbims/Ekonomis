package com.hezapp.ekonomis.core.presentation.utils

import android.annotation.SuppressLint
import android.icu.util.Calendar
import java.text.SimpleDateFormat

fun Long.toMyDateString() : String {
    val currentDate = Calendar.getInstance().also { it.timeInMillis = this }
    return myDateFormatter.format(currentDate.time)
}

fun Long.toShortDateString() : String {
    val currentDate = Calendar.getInstance().also { it.timeInMillis = this }
    return myShortDateFormatter.format(currentDate.time)
}

@SuppressLint("SimpleDateFormat")
private val myDateFormatter = SimpleDateFormat().apply {
    applyPattern("E, dd-MMM-yyyy")
}

@SuppressLint("SimpleDateFormat")
private val myShortDateFormatter = SimpleDateFormat().apply {
    applyPattern("dd/M/yyyy")
}