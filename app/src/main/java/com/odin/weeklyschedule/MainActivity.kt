package com.odin.weeklyschedule

import android.graphics.Color
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.odin.weeklycalendar.data.LongClickMenuData
import com.odin.weeklycalendar.data.ScheduleData
import com.odin.weeklycalendar.util.*
import com.odin.weeklycalendar.views.SchedulerView
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.*

class MainActivity : AppCompatActivity() {

    private val schedulerView: SchedulerView by lazy { findViewById(R.id.week_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSchedulerView()
        setupListener()
    }

    private fun setupSchedulerView() {
        schedulerView.run {
            setLessonClickListener {
                Toast.makeText(applicationContext, "제거" + it.schedule.contents, Toast.LENGTH_SHORT)
                    .show()
                schedulerView.removeView(it)
            }
        }
        registerForContextMenu(schedulerView)
    }

    private fun setupListener() {
        findViewById<Button>(R.id.add_btn).setOnClickListener {
            schedulerView.addEvent(createRandomEvent())
        }
    }

    private val weekDays = DayOfWeek.values().toList()
    private val random = Random()
    private fun createRandomEvent(): ScheduleData {
        val startTime = LocalTime.of(
            OPEN_HOURS + random.nextInt(OPENNING_HOURS), random.nextInt(
                RESERVE_MAX_MINUTE
            )
        )
        val endTime =
            startTime.plusMinutes((RESERVE_MIN_MINUTE + random.nextInt(RESERVE_MAX_MINUTE)).toLong())

        val day = weekDays.shuffled().first()
        return createSampleEntry(day, startTime, endTime)
    }

    private fun createSampleEntry(
        day: DayOfWeek,
        startTime: LocalTime,
        endTime: LocalTime
    ): ScheduleData {
        val name = titles[random.nextInt(titles.size)]
        val subTitle = subTitles[random.nextInt(subTitles.size)]
        return ScheduleData(
            id = random.nextLong(),
            date = LocalDate.now().with(day),
            contents = name,
            subTitle = subTitle,
            startTime = startTime,
            endTime = endTime,
            textColor = Color.WHITE,
            backgroundColor = randomColor(),
            isHoliday = false
        )
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo
    ) {
        val (event) = menuInfo as LongClickMenuData
        menu.setHeaderTitle(event.contents)
        menu.add(0, 0, 0, "예약변경")
        menu.add(0, 1, 1, "예약취소")
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                Toast.makeText(this, "예약변경", Toast.LENGTH_LONG).show()
            }
            1 -> {
                Toast.makeText(this, "예약취소", Toast.LENGTH_LONG).show()
            }
        }
        return true
    }
}