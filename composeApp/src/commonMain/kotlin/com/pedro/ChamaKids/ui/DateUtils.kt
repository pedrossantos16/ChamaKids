package com.pedro.ChamaKids.ui

import kotlinx.datetime.*

fun adjustPickerDateToLocalMillis(utcMillis: Long): Long {
    val pickedDate = Instant.fromEpochMilliseconds(utcMillis).toLocalDateTime(TimeZone.UTC).date
    val nowTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val localDateTime = LocalDateTime(
        year = pickedDate.year,
        monthNumber = pickedDate.monthNumber,
        dayOfMonth = pickedDate.dayOfMonth,
        hour = nowTime.hour,
        minute = nowTime.minute,
        second = nowTime.second
    )
    return localDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}
