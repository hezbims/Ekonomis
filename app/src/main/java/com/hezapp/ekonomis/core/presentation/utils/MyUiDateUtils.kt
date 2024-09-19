package com.hezapp.ekonomis.core.presentation.utils

import android.annotation.SuppressLint
import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toMyDateString() : String {
    val currentDate = Calendar.getInstance().also { it.timeInMillis = this }
    return myDateFormatter.format(currentDate.time)
}

fun Long.toShortDateString() : String {
    val currentDate = Calendar.getInstance().also { it.timeInMillis = this }
    return myShortDateFormatter.format(currentDate.time)
}

fun Long.toFullMonthYearString() : String =
    myFullMonthYearFormatter.format(
        Calendar.getInstance().also { it.timeInMillis = this }.time
    )

fun Long.toShortMonthYearString() : String =
    myShortMonthYearFormatter.format(
        Calendar.getInstance().also { it.timeInMillis = this }.time
    )


@SuppressLint("SimpleDateFormat")
private val myDateFormatter = SimpleDateFormat().apply {
    applyPattern("E, dd-MMM-yyyy")
}

@SuppressLint("SimpleDateFormat")
private val myShortDateFormatter = SimpleDateFormat().apply {
    applyPattern("dd/M/yyyy")
}

private val myFullMonthYearFormatter = SimpleDateFormat("MMMM yyyy", Locale.US)

private val myShortMonthYearFormatter = SimpleDateFormat("MMM yyyy", Locale.US)