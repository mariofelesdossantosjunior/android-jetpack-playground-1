package com.jeppeman.jetpackplayground.extensions

fun Int.timeFormat(): String {
    val totalSeconds = this / 1000
    val totalMinutes = totalSeconds / 60
    val totalHours = totalMinutes / 60
    val secondsInMinute = totalSeconds % 60
    val minutesInHour = totalMinutes % 60

    val secondsString = if (secondsInMinute < 10) "0$secondsInMinute" else secondsInMinute.toString()
    val minutesString = if (minutesInHour < 10) "0$minutesInHour:" else "$minutesInHour:"
    val hoursString = when {
        totalHours == 0 -> ""
        totalHours < 10 -> "0$totalHours:"
        else -> "$totalHours:"
    }

    return "$hoursString$minutesString$secondsString"
}