package com.odin.weeklycalendar.util

import android.graphics.Paint
import android.graphics.Rect
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

object Utils {

    fun textSize(text: String, maxTextSize: Float, maxWidth: Int, maxHeight: Int): Float {
        var hi = maxTextSize
        var lo = 12f
        val threshold = 0.5f

        val paint = Paint()
        val bounds = Rect()

        while (hi - lo > threshold) {
            val size = (hi + lo) / 2
            paint.textSize = size
            paint.getTextBounds(text, 0, text.length, bounds)
            if (bounds.width() >= maxWidth || bounds.height() >= maxHeight) {
                hi = size
            } else {
                lo = size
            }
        }
        return lo
    }


    fun LocalTime.toLocalString(): String {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(this)
    }

}