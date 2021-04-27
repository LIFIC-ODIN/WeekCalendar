package com.odin.weeklycalendar.data

import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

data class ScheduleData(
    val id: Long,
    val date: LocalDate,
    val contents: String,
    val isHoliday: Boolean,
    val subTitle: String? = null,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val upperText: String? = null,
    val lowerText: String? = null,
    val textColor: Int,
    val backgroundColor: Int,
    val eventTag: String = ""
) {
    val duration: Duration = Duration.between(startTime, endTime)
}