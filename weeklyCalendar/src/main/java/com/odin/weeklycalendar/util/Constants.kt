package com.odin.weeklycalendar.util

import android.graphics.Color
import java.util.*

const val OPEN_HOURS = 9
const val END_HOURS = 22
const val OPENNING_HOURS = END_HOURS - OPEN_HOURS

const val RESERVE_MIN_MINUTE = 30
const val RESERVE_MAX_MINUTE = 60

val titles = listOf("홍길동", "임꺽정", "김명성", "강동원", "조인성", "이지은", "한지민")
val subTitles = listOf("다운펌", "볼륨매직", "커트", "뿌리염색", null)

val random = Random()
fun randomColor(): Int {
    return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
}

const val LIST_FORMAT = 6L        //일주일
//const val LIST_FORMAT = 2L          //3일


const val weightStartTime = 1
const val weightUpperText = 1
const val weightTitle = 3
const val weightSubTitle = 1
const val weightLowerText = 1
const val weightEndTime = 1

val weightSum: Int =
    weightStartTime + weightUpperText + weightSubTitle + weightLowerText + weightEndTime + weightTitle