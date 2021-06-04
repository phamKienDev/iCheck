package vn.icheck.android.ichecklibs

import android.app.AlarmManager
import java.text.SimpleDateFormat
import java.util.*

object TimeHelper {

    val simpleDateFormatSv: SimpleDateFormat
        get() {
            val sdfSv = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            sdfSv.timeZone = TimeZone.getTimeZone("GMT+0")
            return sdfSv
        }

    private fun simpleDateFormatSv(format: String = "yyyy-MM-dd'T'HH:mm:ss"): SimpleDateFormat {
        val sdfSv = SimpleDateFormat(format, Locale.getDefault())
        sdfSv.timeZone = TimeZone.getTimeZone("GMT+0")
        return sdfSv
    }

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

    fun convertDateVnToMillisecond2(dateVn: String?): Long? {
        if (dateVn.isNullOrEmpty())
            return null

        val sdfSv = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())

        return try {
            sdfSv.parse(dateVn)?.time
        } catch (e: Exception) {
            null
        }
    }

    fun convertMillisecondToDateTimeSv(millisecond: Long?): String? {
        if (millisecond == null || millisecond == -1L)
            return null

        return try {
            simpleDateFormatSv.format(Date(millisecond))
        } catch (e: Exception) {
            null
        }
    }
}