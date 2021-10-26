package com.shefrengo.health.utils.extentions

import android.util.Log
import com.shefrengo.health.Constants
import com.shefrengo.health.utils.DateTimeUnits


import java.text.ParseException
import java.util.*
import java.util.concurrent.TimeUnit

private fun getFormattedDay(dayOfMonth: Int): String {
    return if (dayOfMonth > 9)
        dayOfMonth.toString()
    else
        "0$dayOfMonth"
}

fun getTodayDate(): String = Constants.DATE_FORMAT.format(Date())

fun getDateFormat(dayOfMonth: Int, monthOfYear: Int, year: Int): String =
    getFormattedDay(dayOfMonth) + "-" + getFormattedDay(monthOfYear + 1) + "-" + year.toString()

fun getFormattedTime(hourOfDay: Int, minute: Int): String = getFormattedDay(hourOfDay) + ":" + getFormattedDay(minute)

@Throws(ParseException::class)
fun isDateAfter(fromDate: String, toDate: String): Boolean {
    try {
        return Constants.DATE_FORMAT.parse(fromDate).after(Constants.DATE_FORMAT.parse(toDate))
    } catch (e: ParseException) {
        Log.d("ShadowTransformer", "ShadowTransformer")
    }
    return false
}

@Throws(ParseException::class)
fun isDateBefore(fromDate: String, toDate: String): Boolean {
    try {
        return Constants.DATE_FORMAT.parse(fromDate).before(Constants.DATE_FORMAT.parse(toDate))
    } catch (e: ParseException) {
        Log.d("ShadowTransformer", "ShadowTransformer")
    }
    return false
}

fun getDateDiff(nowDate: Date, oldDate: Date, dateDiff: DateTimeUnits): Int {
    val diffInMs = nowDate.time - oldDate.time
    val days = TimeUnit.MILLISECONDS.toDays(diffInMs)
    val hours = (TimeUnit.MILLISECONDS.toHours(diffInMs) - TimeUnit.DAYS.toHours(days))
    val minutes =
        (TimeUnit.MILLISECONDS.toMinutes(diffInMs) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diffInMs)))
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
    return when (dateDiff) {
        DateTimeUnits.DAYS -> days.toInt()
        DateTimeUnits.SECONDS -> seconds.toInt()
        DateTimeUnits.MINUTES -> minutes.toInt()
        DateTimeUnits.HOURS -> hours.toInt()
        else -> diffInMs.toInt()
    }
}

fun toDate(string: String, currentFormat: Int = Constants.DateFormatCodes.YMD_HMS): String {
    return when (currentFormat) {
        Constants.DateFormatCodes.YMD_HMS -> Constants.DD_MMM_YYYY.format(Constants.FULL_DATE_FORMATTER.parse(string))
        Constants.DateFormatCodes.YMD -> Constants.DD_MMM_YYYY.format(Constants.YYYY_MM_DD.parse(string))
        else -> string
    }
}