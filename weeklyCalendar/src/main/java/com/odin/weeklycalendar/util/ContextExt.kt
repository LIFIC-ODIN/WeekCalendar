package com.odin.weeklycalendar.util

import android.content.Context
import android.util.TypedValue

fun Context.dpToPixelFloat(dip: Float): Float {
    val metrics = resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics)
}

fun Context.dipToPixelInt(dip: Float): Int {
    return dpToPixelFloat(dip).toInt()
}