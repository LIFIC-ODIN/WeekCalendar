package com.odin.weeklycalendar.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.RelativeLayout
import com.odin.weeklycalendar.data.ScheduleData
import com.odin.weeklycalendar.util.DayOfWeekUtil
import com.odin.weeklycalendar.util.dpToPixelFloat
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration
import kotlin.math.roundToInt

class SchedulerView(
    context: Context,
    attributeSet: AttributeSet
) : RelativeLayout(context, attributeSet) {

    private val backgroundView: SchedulerBackgroundView = SchedulerBackgroundView(context)
    private val overlapsWith = ArrayList<EventItemView>()

    private var clickListener: ((itemView: EventItemView) -> Unit)? = null
    private var contextMenuListener: OnCreateContextMenuListener? = null
    private var eventTransitionName: String? = null

    private val scaleGestureDetector: ScaleGestureDetector

    init {
        addView(backgroundView)
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val factor = detector.scaleFactor
            val scaleFactor = 0.25f.coerceAtLeast(factor.coerceAtMost(3.0f))
            backgroundView.scalingFactor = scaleFactor
            invalidate()
            requestLayout()
            return true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        super.dispatchTouchEvent(event)
        return scaleGestureDetector.onTouchEvent(event)
    }

    fun setLessonClickListener(clickListener: (itemView: EventItemView) -> Unit) {
        this.clickListener = clickListener
        for (childIndex in 0 until childCount) {
            val view: View = getChildAt(childIndex)
            if (view is EventItemView) {
                view.setOnClickListener {
                    clickListener.invoke(view)
                }
            }
        }
    }

    override fun setOnCreateContextMenuListener(contextMenuListener: OnCreateContextMenuListener?) {
        this.contextMenuListener = contextMenuListener
        for (childIndex in 0 until childCount) {
            val view: View = getChildAt(childIndex)
            if (view is EventItemView) {
                view.setOnCreateContextMenuListener(contextMenuListener)
            }
        }
    }

    fun addEvent(schedule: ScheduleData) {
        when (schedule.date.dayOfWeek) {
            DayOfWeek.SATURDAY -> {
                if (!backgroundView.days.contains(DayOfWeek.SATURDAY)) {
                    backgroundView.days.add(DayOfWeek.SATURDAY)
                }
            }
            DayOfWeek.SUNDAY -> {
                if (!backgroundView.days.contains(DayOfWeek.SATURDAY)) {
                    backgroundView.days.add(DayOfWeek.SATURDAY)
                }
                if (!backgroundView.days.contains(DayOfWeek.SUNDAY)) {
                    backgroundView.days.add(DayOfWeek.SUNDAY)
                }
            }
            else -> {
                // nothing to do, just add the event
            }
        }

        val eventItemView = EventItemView(context, schedule)
        backgroundView.updateTimes(schedule.startTime, schedule.endTime)
        eventItemView.setOnClickListener { clickListener?.invoke(eventItemView) }
        eventItemView.setOnCreateContextMenuListener(contextMenuListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            eventItemView.transitionName = eventTransitionName
        }

        addView(eventItemView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        Log.v(TAG, "Measuring ($widthSize x $heightSize)")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(true, l, t, r, b)

        for (childIndex in 0 until childCount) {
            val eventItemView: EventItemView
            val childView = getChildAt(childIndex)
            if (childView is EventItemView) {
                eventItemView = childView
            } else {
                continue
            }

            val column: Int = DayOfWeekUtil.mapDayToColumn(eventItemView.schedule.date.dayOfWeek)
            if (column < 0) {
                childView.setVisibility(View.GONE)
                removeView(childView)
                continue
            }
            var left: Int = backgroundView.getColumnStart(column, true)
            val right: Int = backgroundView.getColumnEnd(column, true)

            overlapsWith.clear()
            for (j in 0 until childIndex) {
                val v2 = getChildAt(j)
                if (v2 is EventItemView) {
                    if (v2.schedule.date != eventItemView.schedule.date) {
                        continue
                    } else if (overlaps(eventItemView, v2)) {
                        overlapsWith.add(v2)
                    }
                }
            }

            if (overlapsWith.size > 0) {
                val width = (right - left) / (overlapsWith.size + 1)
                for ((index, view) in overlapsWith.withIndex()) {
                    val left2 = left + index * width
                    view.layout(left2, view.top, left2 + width, view.bottom)
                }
                left = right - width
            }

            eventItemView.scalingFactor = 1f
            val startTime = backgroundView.startTime
            val lessonStart = eventItemView.schedule.startTime
            val offset = Duration.between(startTime, lessonStart)

            val yOffset = offset.toMinutes() * 1f
            val top = context.dpToPixelFloat(yOffset) + backgroundView.topOffsetPx

            val bottom = top + eventItemView.measuredHeight
            eventItemView.layout(left, top.roundToInt(), right, bottom.roundToInt())
        }
    }

    private fun overlaps(left: EventItemView, right: EventItemView): Boolean {
        val rightStartsAfterLeftStarts = right.schedule.startTime >= left.schedule.startTime
        val rightStartsBeforeLeftEnds = right.schedule.startTime < left.schedule.endTime
        val lessonStartsWithing = rightStartsAfterLeftStarts && rightStartsBeforeLeftEnds

        val leftStartsBeforeRightEnds = left.schedule.startTime < right.schedule.endTime
        val rightEndsBeforeOrWithLeftEnds = right.schedule.endTime <= left.schedule.endTime
        val lessonEndsWithing = leftStartsBeforeRightEnds && rightEndsBeforeOrWithLeftEnds

        val leftStartsAfterRightStarts = left.schedule.startTime > right.schedule.startTime
        val rightEndsAfterLeftEnds = right.schedule.endTime > left.schedule.endTime
        val lessonWithin = leftStartsAfterRightStarts && rightEndsAfterLeftEnds

        return lessonStartsWithing || lessonEndsWithing || lessonWithin
    }

    companion object {
        private const val TAG = "WeekView"
    }
}