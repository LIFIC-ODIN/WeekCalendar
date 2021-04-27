package com.odin.weeklycalendar.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import com.odin.weeklycalendar.util.DayOfWeekUtil
import com.odin.weeklycalendar.util.Utils.toLocalString
import com.odin.weeklycalendar.util.dpToPixelFloat
import com.odin.weeklycalendar.util.dipToPixelInt
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.util.*
import kotlin.math.roundToInt

internal class SchedulerBackgroundView constructor(context: Context) : View(context) {

    private val todayPaint: Paint by lazy {
        Paint().apply {
            strokeWidth = DIVIDER_WIDTH_PX.toFloat() * 2
            color = Color.RED
        }
    }

    private val paintDivider: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = DIVIDER_WIDTH_PX.toFloat()
            color = Color.LTGRAY
        }
    }

    private val mPaintLabel: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            textSize = context.dpToPixelFloat(12f)
            textAlign = Paint.Align.CENTER
        }
    }

    val topOffsetPx: Int = context.dipToPixelInt(32f)
    private val leftOffset: Int = context.dipToPixelInt(48f)

    val days: MutableList<DayOfWeek> = DayOfWeekUtil.createList().toMutableList()

    var startTime = LocalTime.of(10, 0)
        private set
    private var endTime = LocalTime.of(13, 0)

    var scalingFactor = 1f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)

        canvas.drawHorizontalDividers()
        canvas.drawColumnsWithHeaders()

    }

    private fun Canvas.drawHorizontalDividers() {
        var localTime = startTime
        var last = LocalTime.MIN
        while (localTime.isBefore(endTime) && !last.isAfter(localTime)) {
            val offset = Duration.between(startTime, localTime)
            val y = topOffsetPx + context.dpToPixelFloat(offset.toMinutes() * scalingFactor)
            drawLine(0f, y, width.toFloat(), y, paintDivider)

            val timeString = localTime.toLocalString()
            drawMultiLineText(
                this,
                timeString,
                context.dpToPixelFloat(25f),
                y + context.dpToPixelFloat(20f),
                mPaintLabel
            )

            last = localTime
            localTime = localTime.plusHours(1)
        }
        drawLine(0f, bottom.toFloat(), width.toFloat(), bottom.toFloat(), paintDivider)
    }

    private fun drawMultiLineText(
        canvas: Canvas,
        text: String,
        initialX: Float,
        initialY: Float,
        paint: Paint
    ) {
        var currentY = initialY
        text.split(" ")
            .dropLastWhile(String::isEmpty)
            .forEach {
                canvas.drawText(it, initialX, currentY, paint)
                currentY += (-paint.ascent() + paint.descent()).toInt()
            }
    }


    private fun Canvas.drawColumnsWithHeaders() {
        val todayDay: DayOfWeek = LocalDate.now().dayOfWeek
        for ((column, dayId) in days.withIndex()) {
            drawLeftColumnDivider(column)
            drawWeekDayName(dayId, column)
            if (dayId == todayDay) {
                drawDayHighlight(column)
            }
        }
    }

    private fun Canvas.drawLeftColumnDivider(column: Int) {
        val left: Int = getColumnStart(column, false)
        drawLine(left.toFloat(), 0f, left.toFloat(), bottom.toFloat(), paintDivider)
    }

    private fun Canvas.drawDayHighlight(column: Int) {
        val left2: Int = getColumnStart(column, true)
        val right: Int = getColumnEnd(column, true)
        val rect = Rect(left2, 0, right, bottom)
        todayPaint.alpha = 32
        drawRect(rect, todayPaint)
    }

    private fun Canvas.drawWeekDayName(day: DayOfWeek, column: Int) {
        val name = day.value
        val shortName = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val xLabel = (getColumnStart(column, false) + getColumnEnd(column, false)) / 2
        drawText(shortName+"$name", xLabel.toFloat(), topOffsetPx / 2 + mPaintLabel.descent(), mPaintLabel)
    }

    fun getColumnStart(column: Int, considerDivider: Boolean): Int {
        val contentWidth: Int = width - leftOffset
        var offset: Int = leftOffset + contentWidth * column / days.size
        if (considerDivider) {
            offset += (DIVIDER_WIDTH_PX / 2)
        }
        return offset
    }

    fun getColumnEnd(column: Int, considerDivider: Boolean): Int {
        val contentWidth: Int = width - leftOffset
        var offset: Int = leftOffset + contentWidth * (column + 1) / days.size
        if (considerDivider) {
            offset -= (DIVIDER_WIDTH_PX / 2)
        }
        return offset
    }

    override fun onMeasure(widthMeasureSpec: Int, hms: Int) {
        val height =
            topOffsetPx + context.dpToPixelFloat(getDurationMinutes() * scalingFactor) + paddingBottom
        val heightMeasureSpec2 =
            MeasureSpec.makeMeasureSpec(height.roundToInt(), MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec2)
    }

    private fun getDurationMinutes(): Long {
        return Duration.between(startTime, endTime).toMinutes()
    }

    fun updateTimes(startTime: LocalTime, endTime: LocalTime) {
        if (startTime.isAfter(endTime)) {
            return
        }
        var timesHaveChanged = false
        if (startTime.isBefore(this.startTime)) {
            this.startTime = startTime.truncatedTo(ChronoUnit.HOURS)
            timesHaveChanged = true
        }
        if (endTime.isAfter(this.endTime)) {
            if (endTime.isBefore(LocalTime.of(23, 0))) {
                this.endTime = endTime.truncatedTo(ChronoUnit.HOURS).plusHours(1)
            } else {
                this.endTime = LocalTime.MAX
            }
            timesHaveChanged = true
        }
        if (this.startTime.isAfter(this.endTime)) throw IllegalArgumentException()

        if (timesHaveChanged) {
            requestLayout()
        }
    }

    companion object {
        private const val DIVIDER_WIDTH_PX: Int = 2
    }
}