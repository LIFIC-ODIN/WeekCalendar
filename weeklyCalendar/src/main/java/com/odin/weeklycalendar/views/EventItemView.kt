package com.odin.weeklycalendar.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.PaintDrawable
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.odin.weeklycalendar.data.LongClickMenuData
import com.odin.weeklycalendar.data.ScheduleData
import com.odin.weeklycalendar.util.*
import com.odin.weeklycalendar.util.Utils.textSize
import com.odin.weeklycalendar.util.Utils.toLocalString
import kotlin.math.roundToInt

@SuppressLint("ViewConstructor")
class EventItemView(
    context: Context,
    val schedule: ScheduleData
) : View(context) {

    private val TAG = javaClass.simpleName
    private val CORNER_RADIUS_PX = context.dpToPixelFloat(2f)

    private val textPaint: Paint by lazy { Paint().apply { isAntiAlias = true } }
    private val subjectName: String by lazy { schedule.contents }

    private val textBounds: Rect = Rect()

    var scalingFactor: Float = 1f

    init {
        val padding = this.context.dipToPixelInt(2f)
        setPadding(padding, padding, padding, padding)

        background = PaintDrawable().apply {
            paint.color = schedule.backgroundColor
            setCornerRadius(CORNER_RADIUS_PX)
        }
        textPaint.color = schedule.textColor
    }

    override fun onDraw(canvas: Canvas) {
        Log.d(TAG, "Drawing ${schedule.contents}")
        // title
        val maxTextSize = textSize(
            subjectName,
            textPaint.textSize * 3,
            width - (paddingLeft + paddingRight),
            height / 4
        )
        textPaint.textSize = maxTextSize
        textPaint.getTextBounds(subjectName, 0, subjectName.length, textBounds)
        var weight = weightStartTime + weightUpperText
        if (weight == 0) {
            weight++
        }
        val subjectY = getY(weight, weightTitle, textBounds)
        canvas.drawText(
            subjectName,
            (width / 2 - textBounds.centerX()).toFloat(),
            subjectY.toFloat(),
            textPaint
        )

        textPaint.textSize = textSize(
            "", maxTextSize, width / 2,
            getY(position = 1, bounds = textBounds) - getY(position = 0, bounds = textBounds)
        )

        // start time
        val startText = schedule.startTime.toLocalString()
        textPaint.getTextBounds(startText, 0, startText.length, textBounds)
        canvas.drawText(
            startText,
            (textBounds.left + paddingLeft).toFloat(),
            (textBounds.height() + paddingTop).toFloat(),
            textPaint
        )

        // end time
        val endText = schedule.endTime.toLocalString()
        textPaint.getTextBounds(endText, 0, endText.length, textBounds)
        canvas.drawText(
            endText,
            (width - (textBounds.right + paddingRight)).toFloat(),
            (height - paddingBottom).toFloat(),
            textPaint
        )

        // upper text
        if (schedule.upperText != null) {
            textPaint.getTextBounds(schedule.upperText, 0, schedule.upperText.length, textBounds)
            val typeY = getY(position = weightStartTime, bounds = textBounds)
            canvas.drawText(
                schedule.upperText,
                (width / 2 - textBounds.centerX()).toFloat(),
                typeY.toFloat(),
                textPaint
            )
        }

        // subtitle
        if (schedule.subTitle != null) {
            textPaint.getTextBounds(schedule.subTitle, 0, schedule.subTitle.length, textBounds)
            val teacherY = getY(
                position = weightStartTime + weightUpperText + weightTitle,
                bounds = textBounds
            )
            canvas.drawText(
                schedule.subTitle,
                (width / 2 - textBounds.centerX()).toFloat(),
                teacherY.toFloat(),
                textPaint
            )
        }

        // lower text
        if (schedule.lowerText != null) {
            textPaint.getTextBounds(schedule.lowerText, 0, schedule.lowerText.length, textBounds)
            val locationY = getY(
                position = weightStartTime + weightUpperText + weightTitle + weightSubTitle,
                bounds = textBounds
            )
            canvas.drawText(
                schedule.lowerText,
                (width / 2 - textBounds.centerX()).toFloat(),
                locationY.toFloat(),
                textPaint
            )
        }
    }

    private fun getY(position: Int, weight: Int = 1, bounds: Rect): Int {
        val content = height - (paddingTop + paddingBottom)
        val y = (content * (position + 0.5f * weight) / weightSum) + paddingTop
        return y.roundToInt() - bounds.centerY()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeightDp = schedule.duration.toMinutes() * scalingFactor
        val desiredHeightPx = context.dipToPixelInt(desiredHeightDp)
        val resolvedHeight = resolveSize(desiredHeightPx, heightMeasureSpec)

        setMeasuredDimension(width, resolvedHeight)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val anim = ScaleAnimation(
            0f, 1f,
            0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            fillAfter = true
            duration = 500
        }
        this.startAnimation(anim)
    }

    override fun getContextMenuInfo(): ContextMenu.ContextMenuInfo {
        return LongClickMenuData(schedule)
    }
}
