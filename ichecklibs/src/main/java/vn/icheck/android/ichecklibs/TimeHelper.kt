package vn.icheck.android.ichecklibs

import android.app.AlarmManager
import java.text.SimpleDateFormat
import java.util.*

object TimeHelper {
    fun getCreteTimeDate(): String {
        val sdf = SimpleDateFormat("HH_mm_ss_dd_MM_yyyy", Locale.getDefault())
        val mDate = Date()
        return sdf.format(mDate)
    }

    fun convertMillisecondToTime(millisecond: Long): String {
        val hour = millisecond / AlarmManager.INTERVAL_HOUR
        val minute = (millisecond - (hour * AlarmManager.INTERVAL_HOUR)) / 60000
        val second = (millisecond - (hour * AlarmManager.INTERVAL_HOUR) - (minute * 60000)) / 1000
        return if (hour > 0) {
            "${String.format("%02d", hour)}:${String.format("%02d", minute)}:${String.format("%02d", second)}"
        } else {
            "${String.format("%02d", minute)}:${String.format("%02d", second)}"
        }
    }

}