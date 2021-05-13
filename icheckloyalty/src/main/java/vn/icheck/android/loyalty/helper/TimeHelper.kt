package vn.icheck.android.loyalty.helper

import android.app.AlarmManager
import java.text.SimpleDateFormat
import java.util.*

object TimeHelper {

    private val intervalHour = 60 * 60 * 1000
    private val intervalMinute = 60 * 1000

    private val simpleDateFormatSv: SimpleDateFormat
        get() {
            val sdfSv = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            sdfSv.timeZone = TimeZone.getTimeZone("GMT")
            return sdfSv
        }

    private fun simpleDateFormatVn(format: String): SimpleDateFormat {
        val sdfSv = SimpleDateFormat(format)
        sdfSv.timeZone = TimeZone.getTimeZone("GMT+07")
        return sdfSv
    }

    fun convertDateTimeSvToTimeDateVn(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        return try {
            val mDate = simpleDateFormatSv.parse(date)
            simpleDateFormatVn("HH:mm dd/MM/yyyy").format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToDateVn(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        return try {
            simpleDateFormatVn("dd/MM/yyyy").format(simpleDateFormatSv.parse(date))
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToMillisecond(date: String?): Long? {
        if (date.isNullOrEmpty())
            return null

        return try {
            simpleDateFormatSv.parse(date)?.time
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToCurrentDate(millisecond: Long?): String {
        val time = (millisecond ?: 0) - System.currentTimeMillis()

        return if (time > 0) {
            when {
                time <= intervalMinute -> {
                    (time / 1000).toString() + " giây"
                }
                time <= intervalHour -> {
                    (time / intervalMinute).toString() + " phút"
                }
                time < AlarmManager.INTERVAL_DAY -> {
                    (time / intervalHour).toString() + " giờ" +
                            " ${
                                if ((time % intervalHour) != 0L) {
                                    ((time % intervalHour) / intervalMinute).toString() + " phút"
                                } else {
                                    ""
                                }
                            }"
                }
                else -> {
                    (time / AlarmManager.INTERVAL_DAY).toString() + " ngày" +
                            " ${
                                if ((time % AlarmManager.INTERVAL_DAY) != 0L) {
                                    if ((time % AlarmManager.INTERVAL_DAY) / intervalHour != 0L) {
                                        ((time % AlarmManager.INTERVAL_DAY) / intervalHour).toString() + " giờ"
                                    } else {
                                        ""
                                    }
                                } else {
                                    ""
                                }
                            }"
                }
            }
        } else {
            ""
        }
    }

    fun millisecondEffectiveTime(effective_type: String, effective_time: String, released_at: String): Long {
        val millisecond = when (effective_type) {
            "minutes" -> {
                effective_time.toLong() * 60 * 1000
            }
            "hour" -> {
                effective_time.toLong() * 60 * 60 * 1000
            }
            "day" -> {
                effective_time.toLong() * AlarmManager.INTERVAL_DAY
            }
            "month" -> {
                effective_time.toLong() * 30 * AlarmManager.INTERVAL_DAY
            }
            "year" -> {
                effective_time.toLong() * 365 * AlarmManager.INTERVAL_DAY
            }
            else -> {
                0
            }
        }

        return (convertDateTimeSvToMillisecond(released_at) ?: 0) + millisecond
    }
}