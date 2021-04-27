package com.odin.weeklycalendar.util

import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
import java.util.*


object DayOfWeekUtil {
    fun createList(
        firstDay: DayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    ): List<DayOfWeek> =
        (0..LIST_FORMAT).toList().map { firstDay.plus(it) }

//    fun mapDayToColumn(day: DayOfWeek): Int {
//        when (val firstDayOfTheWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek) {
//
//            DayOfWeek.MONDAY -> {
//                // mo: 0, fr:4, su:6
//                val column = day.value
//                return column - 1
//            }
//
//            DayOfWeek.SATURDAY -> {
//                // sa: 0, su: 1, fr: 6,
//                return when (day) {
//                    DayOfWeek.SATURDAY -> 0
//                    DayOfWeek.SUNDAY -> 1
//                    else -> day.value + 1
//                }
//            }
//            DayOfWeek.SUNDAY -> {
//                // su: 0, mo: 1 fr: 5, sa: 6
//                return if (day == DayOfWeek.SUNDAY) {
//                    0
//                } else {
//                    day.value
//                }
//            }
//            else -> throw IllegalStateException("$firstDayOfTheWeek das is not supported as start day")
//        }
//    }

    fun mapDayToColumn(day: DayOfWeek): Int = day.value

}