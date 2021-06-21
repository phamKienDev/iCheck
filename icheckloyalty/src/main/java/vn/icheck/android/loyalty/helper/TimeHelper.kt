package vn.icheck.android.loyalty.helper

import android.app.AlarmManager
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.model.ICKVoucher
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
                    getString(R.string.s_giay, (time / 1000).toString())
                }
                time <= intervalHour -> {
                    getString(R.string.s_phut,(time / intervalMinute).toString())
                }
                time < AlarmManager.INTERVAL_DAY -> {
                    getString(R.string.s_gio,(time / intervalHour).toString()) +
                            " ${
                                if ((time % intervalHour) != 0L) {
                                    getString(R.string.s_phut,((time % intervalHour) / intervalMinute).toString())
                                } else {
                                    ""
                                }
                            }"
                }
                else -> {
                    getString(R.string.s_ngay,(time / AlarmManager.INTERVAL_DAY).toString()) +
                            " ${
                                if ((time % AlarmManager.INTERVAL_DAY) != 0L) {
                                    if ((time % AlarmManager.INTERVAL_DAY) / intervalHour != 0L) {
                                        getString(R.string.s_gio,((time % AlarmManager.INTERVAL_DAY) / intervalHour).toString())
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

    fun timeGiftVoucher(voucher: ICKVoucher): String {
        /**
         * Nếu startAt và endAt khác null thì hiển thị dd/mm/yy
         * Nếu startAt, endAt, releaseAt và effectiveTime khác null nhưng thời gian tổng của startAt và endAt nhỏ hơn thời gian của effectiveTime thì hiển thị: ${Còn xx ngày, xx giờ} theo thời gian hiện tại đến endAt
         * Nếu startAt, endAt, releaseAt và effectiveTime khác null nhưng thời gian tổng của startAt và endAt lớn hơn thời gian của effectiveTime thì hiển thị: ${Còn xx ngày, xx giờ} theo releaseAt và effectiveTime
         */
        return when {
            !voucher.start_at.isNullOrEmpty()
                    && !voucher.end_at.isNullOrEmpty()
                    && (voucher.effective_time.isNullOrEmpty()
                    || voucher.effective_type.isNullOrEmpty()) -> {

                convertDateTimeSvToDateVn(voucher.end_at) ?: ""
            }
            !voucher.released_at.isNullOrEmpty()
                    && !voucher.effective_time.isNullOrEmpty()
                    && !voucher.effective_type.isNullOrEmpty()
                    && (voucher.start_at.isNullOrEmpty()
                    || voucher.end_at.isNullOrEmpty()) -> {


                getString(R.string.con_lai_s, convertDateTimeSvToCurrentDate(millisecondEffectiveTime(voucher.effective_type, voucher.effective_time, voucher.released_at)))
            }
            !voucher.released_at.isNullOrEmpty()
                    && !voucher.effective_time.isNullOrEmpty()
                    && !voucher.effective_type.isNullOrEmpty()
                    && !voucher.start_at.isNullOrEmpty()
                    && !voucher.end_at.isNullOrEmpty() -> {

                val millisecondWithEffectiveTime = millisecondEffectiveTime(voucher.effective_type, voucher.effective_time, voucher.released_at)

                val millisecondWithEffectiveTimeAndCurrent = millisecondEffectiveTime(voucher.effective_type, voucher.effective_time, voucher.released_at) - System.currentTimeMillis()

                val currentMillisecondWithEndAtAndCurrent = (convertDateTimeSvToMillisecond(voucher.end_at) ?: 0) - System.currentTimeMillis()

                if (millisecondWithEffectiveTimeAndCurrent > currentMillisecondWithEndAtAndCurrent) {
                    getString(R.string.con_lai_s, convertDateTimeSvToCurrentDate(convertDateTimeSvToMillisecond(voucher.end_at)))
                } else {
                    getString(R.string.con_lai_s, convertDateTimeSvToCurrentDate(millisecondWithEffectiveTime))
                }
            }
            else -> {
                ""
            }
        }
    }
}