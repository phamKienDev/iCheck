package vn.icheck.android.helper

import android.app.AlarmManager
import android.content.Context
import vn.icheck.android.R
import vn.icheck.android.base.dialog.date_time.callback.DateTimePickerListener
import vn.icheck.android.base.dialog.date_time.date.DatePicker
import vn.icheck.android.base.dialog.date_time.date.DateTimePicker
import vn.icheck.android.base.dialog.date_time.time.TimePicker
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by lecon on 11/26/2017
 */
object TimeHelper {

    fun randInt(min: Int, max: Int): Int {
        // Usually this can be a field rather than a method variable
        val rand = Random()

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt(max - min + 1) + min
    }

    val simpleDateFormatSv: SimpleDateFormat
        get() {
            val sdfSv = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            sdfSv.timeZone = TimeZone.getTimeZone("GMT+0")
            return sdfSv
        }

    private fun simpleDateFormatSv(format: String = "yyyy-MM-dd'T'HH:mm:ss"): SimpleDateFormat {
        val sdfSv = SimpleDateFormat(format)
        sdfSv.timeZone = TimeZone.getTimeZone("GMT+0")
        return sdfSv
    }

    fun simpleDateFormatVn(format: String): SimpleDateFormat {
        val sdfSv = SimpleDateFormat(format)
        sdfSv.timeZone = TimeZone.getTimeZone("GMT+07")
        return sdfSv
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun getCurrentWeekOfMonth(): Int {
        return Calendar.getInstance().get(Calendar.WEEK_OF_MONTH)
    }

    fun getWeekOfMonth(time: String): Int {
        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormatSv.parse(time)
        return calendar.get(Calendar.WEEK_OF_MONTH)
    }

    fun getCreteTimeDate(): String {
        val sdf = SimpleDateFormat("HH_mm_ss_dd_MM_yyyy", Locale.getDefault())
        val mDate = Date()
        return sdf.format(mDate)
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val mDate = Date()
        return sdf.format(mDate)
    }

    /**
     * Chuy???n ?????nh d???ng Date US v??? DateTime Vn
     * @param dateSv String? Date US
     * @return String? Date Vn
     */
    fun convertDateSvToDateVn(dateSv: String?): String? {
        if (dateSv.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("dd/MM/yyyy")

        return try {
            val mDate = sdfSv.parse(dateSv)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeStampVnToDateVn(dateSv: String?): String? {
        if (dateSv.isNullOrEmpty())
            return null

        val sdfDateTimeVn = simpleDateFormatVn("dd/MM/yyyy HH:mm")
        val sdfVn = simpleDateFormatVn("dd/MM/yyyy")

        return try {
            val mDate = sdfDateTimeVn.parse(dateSv)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToDateTimeVn(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("dd/MM/yyyy HH:mm")

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToCurrentTime(date: String?): String? {
        val milisecond = convertDateTimeSvToMillisecond(date) ?: 0
        val currentTime = Date().time
        val time = currentTime - milisecond

        return if (time <= intervalMinute) {
            (time / 1000).toString() + " gi??y tr?????c"
        } else if (time <= intervalHour) {
            (time / intervalMinute).toString() + " ph??t tr?????c"
        } else if (time < AlarmManager.INTERVAL_DAY) {
            (time / intervalHour).toString() + " gi??? tr?????c"
        } else if (time <= (7 * AlarmManager.INTERVAL_DAY)) {
            (time / AlarmManager.INTERVAL_DAY).toString() + " ng??y tr?????c"
        } else {
            convertDateTimeSvToTimeDateVnPhay(date) ?: ""
        }
    }

    fun convertDateTimeSvToTimeDateVn(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("HH:mm dd/MM/yyyy")

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToTimeDateVnStamp(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv("yyyy-MM-dd HH:mm:ss")
        val sdfVn = simpleDateFormatVn("dd/MM/yyyy")

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToTimeDateVnV1(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("HH:mm, dd/MM/yyyy")

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToTimeDateVnV2(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("dd/MM/yyyy ,HH:mm")

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToTimeVn(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("HH:mm")

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToDateTimeVn(date: String?, format: String): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn(format)

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToDayMonthVn(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("dd/MM")

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToTimeDateVnPhay(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("HH:mm, dd/MM/yyyy")

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }


    fun convertDateTimeVnToDateVn(dateSv: String?): String? {
        if (dateSv.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("dd/MM/yyyy")

        return try {
            val mDate = sdfSv.parse(dateSv)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToDateTimeVnStamp(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatVn("HH:mm dd/MM/yyyy")

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeSvToDateVnStamp(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv("yyyy-MM-dd HH:mm:ss")
        val sdfVn = simpleDateFormatVn("dd/MM/yyyy")

        return try {
            sdfVn.format(sdfSv.parse(date))
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeVnToDateSv(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = simpleDateFormatSv
        val sdfVn = simpleDateFormatSv("dd/MM/yyyy")

        return try {
            sdfSv.format(sdfVn.parse(date))
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

    fun convertDateTimeStampVnToMillisecond(date: String?): Long? {
        if (date.isNullOrEmpty())
            return null

        val sdfVn = simpleDateFormatVn("yyyy-MM-dd'T'HH:mm:ss")

        return try {
            sdfVn.parse(date).time
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeStampVnToMillisecond99(date: String?): Long? {
        if (date.isNullOrEmpty())
            return null

        val sdfVn = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return try {
            val mDate = sdfVn.parse(date)
            mDate.time
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeStamp2VnToMillisecond(date: String?): Long? {
        if (date.isNullOrEmpty())
            return null

        val sdfVn = simpleDateFormatVn("dd/MM/yyyy HH:mm")

        return try {
            val mDate = sdfVn.parse(date)
            mDate.time
        } catch (e: Exception) {
            null
        }
    }


    fun convertDateTimeStampVnToDateTimeSv(date: String?): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfVn = simpleDateFormatVn("HH:mm dd/MM/yyyy")

        return try {
            val mDate = sdfVn.parse(date)
            simpleDateFormatSv.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    private val intervalHour = 60 * 60 * 1000
    private val intervalMinute = 60 * 1000

    /**
     * T??nh s??? ng??y, s??? gi??? v?? s??? ph??t
     */
    fun comareDateTimeSvToCurrentTime(date: String?): String? {
        val milisecond = convertDateTimeSvToMillisecond(date)
        val time = System.currentTimeMillis() - (milisecond ?: 0)

        return when {
            time <= intervalMinute -> {
                (time / 1000).toString() + " gi??y"
            }
            time <= intervalHour -> {
                (time / intervalMinute).toString() + " ph??t"
            }
            time < AlarmManager.INTERVAL_DAY -> {
                (time / intervalHour).toString() + " gi???"
            }
            time <= (7 * AlarmManager.INTERVAL_DAY) -> {
                (time / AlarmManager.INTERVAL_DAY).toString() + " ng??y"
            }
            else -> {
                convertDateTimeSvToTimeDateVnPhay(date) ?: ""
            }
        }
    }

    fun comareDateTimeSvToCurrentTimeDay(date: String?): String {
        val milisecond = convertDateTimeSvToMillisecond(date) ?: 0

        val time = milisecond - System.currentTimeMillis()

        val day = time / AlarmManager.INTERVAL_DAY
        val hour = time / intervalHour
        val minute = time / intervalMinute

        return if (time <= intervalMinute) {
            (time / 1000).toString() + " gi??y"
        } else if (time <= intervalHour) {
            "$minute ph??t"
        } else if (time < AlarmManager.INTERVAL_DAY) {
            "$hour gi??? " + "${minute - (hour * 60)} ph??t"
        } else {
            "$day ng??y, " + "${hour - (day * 24)} gi??? " + "${minute - (day * 24 * 60) - ((hour - (day * 24)) * 60)} ph??t"
        }
    }


    fun comareDateTimeVnToCurrentTime(mDate: String?): String? {
        val milisecond = convertDateVnToMillisecond(mDate) ?: 0

        val date = Calendar.getInstance()
        date.timeZone = TimeZone.getTimeZone("GMT+07")

        val time = milisecond - date.timeInMillis

        return if (time <= intervalMinute) {
            (time / 1000).toString() + " gi??y"
        } else if (time <= intervalHour) {
            (time / intervalMinute).toString() + " ph??t"
        } else if (time < AlarmManager.INTERVAL_DAY) {
            (time / intervalHour).toString() + " gi???"
        } else {
            (time / AlarmManager.INTERVAL_DAY).toString() + " ng??y"
        }
    }

    fun convertDateTimeSvToCurrentDate(date: String?): String? {
        val time = System.currentTimeMillis().minus(convertDateTimeSvToMillisecond(date) ?: 0)

        return if (time <= intervalMinute) {
            "V???a xong"
//            if (time / 1000 < 60) {
//                "V???a xong"
//            }
//            else {
//                (time / 1000).toString() + " gi??y tr?????c"
//            }
        } else if (time <= intervalHour) {
            (time / intervalMinute).toString() + " ph??t tr?????c"
        } else if (time < AlarmManager.INTERVAL_DAY) {
            (time / intervalHour).toString() + " gi??? tr?????c"
        }
//        else if (time <= (7 * AlarmManager.INTERVAL_DAY)) {
//            (time / AlarmManager.INTERVAL_DAY).toString() + " ng??y tr?????c"
//        }
        else {
            convertDateTimeSvToTimeDateVnPhay(date) ?: ""
        }
    }

    fun convertDateTimeSvToCurrentDateV2(date: String?): String {
        val time = (convertDateTimeSvToMillisecond(date) ?: 0) - System.currentTimeMillis()

        return when {
            time <= intervalMinute -> {
                (time / 1000).toString() + " gi??y"
            }
            time <= intervalHour -> {
                (time / intervalMinute).toString() + " ph??t"
            }
            time < AlarmManager.INTERVAL_DAY -> {
                (time / intervalHour).toString() + " gi???" +
                        " ${
                            if ((time % intervalHour) != 0L) {
                                ((time % intervalHour) / intervalMinute).toString() + " ph??t"
                            } else {
                                ""
                            }
                        }"
            }
            else -> {
                (time / AlarmManager.INTERVAL_DAY).toString() + " ng??y," +
                        " ${
                            if ((time % AlarmManager.INTERVAL_DAY) != 0L) {
                                ((time % AlarmManager.INTERVAL_DAY) / intervalHour).toString() + " gi???" +
                                        " ${
                                            if ((time % intervalHour) != 0L) {
                                                ((time % intervalHour) / intervalMinute).toString() + " ph??t"
                                            } else {
                                                ""
                                            }
                                        }"
                            } else {
                                ""
                            }
                        }"
            }
        }
    }

    fun convertDateTimeSvToCurrentDay(date: String?): String? {
        val time = System.currentTimeMillis().minus(convertDateTimeSvToMillisecond(date) ?: 0)

        return if (time <= intervalMinute) {
            "V???a xong"
        } else if (time <= intervalHour) {
            (time / intervalMinute).toString() + " ph??t tr?????c"
        } else if (time < AlarmManager.INTERVAL_DAY) {
            (time / intervalHour).toString() + " gi??? tr?????c"
        } else {
            convertDateTimeSvToTimeDateVnPhay(date) ?: ""
        }
    }

    fun convertDateTimeSvToCurrentDay2(date: String?): String? {
        val time = System.currentTimeMillis().minus(convertDateTimeSvToMillisecond(date) ?: 0)

        return if (time <= intervalMinute) {
            "V???a xong"
        } else if (time <= intervalHour) {
            (time / intervalMinute).toString() + " ph??t tr?????c"
        } else if (time < AlarmManager.INTERVAL_DAY) {
            (time / intervalHour).toString() + " gi??? tr?????c"
        } else {
            convertDateTimeVnToDateVn(date) ?: ""
        }
    }

    fun convertDateTimeSvToCurrentTimeDate(date: String?): String? {
        val time = System.currentTimeMillis().minus(convertDateTimeSvToMillisecond(date) ?: 0)

        return if (time <= intervalMinute) {
            (time / 1000).toString() + " gi??y tr?????c"
        } else if (time <= intervalHour) {
            (time / intervalMinute).toString() + " ph??t tr?????c"
        } else if (time < AlarmManager.INTERVAL_DAY) {
            (time / intervalHour).toString() + " gi??? tr?????c"
        } else if (time <= (7 * AlarmManager.INTERVAL_DAY)) {
            (time / AlarmManager.INTERVAL_DAY).toString() + " ng??y tr?????c"
        } else {
            convertDateTimeSvToTimeDateVn(date) ?: ""
        }
    }

    fun convertDateTimeSvToCurrentTimeLeft(date: String?): String {
        val time = (convertDateTimeSvToMillisecond(date) ?: 0).minus(System.currentTimeMillis())

        return if (time <= 0) {
            ""
        } else {
            when {
                time <= intervalMinute -> {
                    "C??n " + (time / 1000).toString() + " gi??y"
                }
                time <= intervalHour -> {
                    "C??n " + (time / intervalMinute).toString() + " ph??t"
                }
                time < AlarmManager.INTERVAL_DAY -> {
                    "C??n " + (time / intervalHour).toString() + " gi???"
                }
                else -> {
                    "C??n " + (time / AlarmManager.INTERVAL_DAY).toString() + " ng??y"
                }
            }
        }
    }

    fun convertDateTimeSvToCurrentTimeLeftCampaign(date: String?): String {
        val endTime = (convertDateTimeSvToMillisecond(date) ?: 0) + (intervalHour * 24)
        val time = endTime.minus(System.currentTimeMillis())

        return if (time <= 0) {
            ""
        } else {
            when {
                time <= intervalMinute -> {
                    "C??n " + (time / 1000).toString() + " gi??y"
                }
                time <= intervalHour -> {
                    "C??n " + (time / intervalMinute).toString() + " ph??t"
                }
                time < AlarmManager.INTERVAL_DAY -> {
                    "C??n " + (time / intervalHour).toString() + " gi???"
                }
                else -> {
                    "C??n " + (time / AlarmManager.INTERVAL_DAY).toString() + " ng??y"
                }
            }
        }
    }


    fun convertMillisecondToCurrentTime(milisecond: Long): String? {
        val time = System.currentTimeMillis() - milisecond

        return if (time < AlarmManager.INTERVAL_DAY) {
            "H??m nay"
        } else if (time <= (7 * AlarmManager.INTERVAL_DAY)) {
            (time / AlarmManager.INTERVAL_DAY).toString() + " ng??y tr?????c"
        } else {
            convertMillisecondToDateVn(milisecond)
        }
    }

    fun convertMillisecondToHour(millisecond: Long): String {
        val hour = millisecond / AlarmManager.INTERVAL_HOUR
        val minute = (millisecond - (hour * AlarmManager.INTERVAL_HOUR)) / 60000
        val second = (millisecond - (hour * AlarmManager.INTERVAL_HOUR) - (minute * 60000)) / 1000
        return "${String.format("%02d", hour)}:${String.format("%02d", minute)}:${String.format("%02d", second)}"
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

    fun convertMillisecondToMissionDetailTime(millisecond: Long): String {
        val day = millisecond / AlarmManager.INTERVAL_DAY
        val hour = (millisecond - (day * AlarmManager.INTERVAL_DAY)) / AlarmManager.INTERVAL_HOUR
        val minute = (millisecond - (day * AlarmManager.INTERVAL_DAY) - (hour * AlarmManager.INTERVAL_HOUR)) / 60000
        return "${String.format("%02d", hour)}:${String.format("%02d", hour)}:${String.format("%02d", minute)}"
    }

    fun convertTotalDayToDateVn(totalDay: Long): String? {
        val year = totalDay / 365
        val totalDayAfterYear = totalDay - (year * 365)
        val month = totalDayAfterYear / 31
        val day = totalDayAfterYear - (month * 31)

        val mYear = if (year > 0) {
            "$year n??m "
        } else {
            ""
        }

        val mMonth = if (month > 0) {
            "$month th??ng "
        } else {
            ""
        }

        val mDay = if (day > 0) {
            "$day ng??y "
        } else {
            ""
        }

        val date = "$mYear$mMonth$mDay"
        return date
    }

    /**
     * Chuy???n ?????nh d???ng Date Vn sang Millisecond
     * @param dateVn String? dd/MM/yyyy
     * @return Long? Millisecond
     */
    fun convertDateVnToMillisecond(dateVn: String?): Long? {
        if (dateVn.isNullOrEmpty())
            return null

        val sdfSv = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return try {
            sdfSv.parse(dateVn)?.time
        } catch (e: Exception) {
            null
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

    fun convertDateVnGMT7ToDateVnGMT(dateVn: String?): String? {
        if (dateVn.isNullOrEmpty())
            return null

        val sdfVn = simpleDateFormatVn("dd/MM/yyyy")
        val sdfSv = simpleDateFormatSv("dd/MM/yyyy")

        return try {
            sdfSv.format(sdfVn.parse(dateVn))
        } catch (e: Exception) {
            null
        }
    }

    fun convertDateTimeVnToMillisecond(dateVn: String?): Long? {
        if (dateVn.isNullOrEmpty())
            return null

        val sdfSv = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        return try {
            sdfSv.parse(dateVn)?.time
        } catch (e: Exception) {
            null
        }
    }


    fun convertDateTimeVnToEventFormat(dateVn: Long?): String? {
        if (dateVn == null)
            return null

        val sdfEvent = SimpleDateFormat("yyyyMMdd'T'HHmm00", Locale.getDefault())

        return try {
            sdfEvent.format(Date(dateVn))
        } catch (e: Exception) {
            null
        }
    }

    fun convertLongToTime(milliseconds: Long?): String? {
        if (milliseconds == null)
            return null

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val mDate = Date(milliseconds)
        return sdf.format(mDate)
    }

    /**
     * Chuy???n ?????nh d???ng Millisecond sang Date Vn
     * @param miliseconds String?
     */
    fun convertLongToDate(miliseconds: Long?): String? {
        if (miliseconds == null)
            return null

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val mDate = Date(miliseconds)
        return sdf.format(mDate)
    }

    /**
     * Chuy???n millisecond sang ?????nh d???nh Date Vn
     * @param millisecond Long? Millisecond
     * @return String? DateTime Vn
     */
    fun convertMillisecondToDateVn(millisecond: Long?): String? {
        if (millisecond == null || millisecond == -1L)
            return null

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return try {
            sdf.format(Date(millisecond))
        } catch (e: Exception) {
            null
        }
    }

    fun convertMillisecondToDateSv(millisecond: Long?): String? {
        if (millisecond == null || millisecond == -1L)
            return null

        val sdf = SimpleDateFormat("HH:mm , dd/MM/yyyy", Locale.getDefault())

        return try {
            sdf.format(Date(millisecond))
        } catch (e: Exception) {
            null
        }
    }

    fun convertMillisecondToDateTimeSv(millisecond: Long?): String? {
        if (millisecond == null || millisecond == -1L)
            return null

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        return try {
            sdf.format(Date(millisecond))
        } catch (e: Exception) {
            null
        }
    }

    fun convertMillisecondToFomateSv(millisecond: Long?, format: String): String? {
        if (millisecond == null || millisecond == -1L)
            return null

        val sdf = SimpleDateFormat(format, Locale.getDefault())

        return try {
            sdf.format(Date(millisecond))
        } catch (e: Exception) {
            null
        }
    }

    /**
     * L???y th???i gian t??? ?????nh d???ng DateTime Vn. VD: ng??y, th??ng, n??m
     * @param date String? DateTime Vn
     * @param timeType String k?? hi???u. VD: yyyy
     * @return String?
     */
    fun getTimeFromDateTimeVn(date: String?, timeType: String): String? {
        if (date.isNullOrEmpty())
            return null

        val sdfSv = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val sdfVn = SimpleDateFormat(timeType, Locale.getDefault())

        return try {
            val mDate = sdfSv.parse(date)
            sdfVn.format(mDate)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * L???y th???i gian t??? millisecond. VD: ng??y, th??ng, n??m
     * @param date String? DateTime Vn
     * @param timeType String k?? hi???u. VD: yyyy
     * @return String?
     */
    fun getTimeFromDateTimeVn(millisecond: Long?, timeType: String): String? {
        if (millisecond == null || millisecond <= 0)
            return null

        val sdfVn = SimpleDateFormat(timeType, Locale.getDefault())

        return try {
            sdfVn.format(Date(millisecond))
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Chuy???n th???i gian ???? ch???n sang millisecond
     * @param day Int? Ng??y
     * @param month Int? Th??ng
     * @param year Int? N??m
     * @param hour Int? Gi???
     * @param minute Int? Ph??t
     * @return Long
     */
    fun convertTimeToLong(day: Int?, month: Int?, year: Int?, hour: Int?, minute: Int?): Long {
        val calendar = Calendar.getInstance()

        day?.let { calendar.set(Calendar.DAY_OF_MONTH, day) }
        month?.let { calendar.set(Calendar.MONTH, month) }
        year?.let { calendar.set(Calendar.YEAR, year) }
        hour?.let { calendar.set(Calendar.HOUR, hour) }
        minute?.let { calendar.set(Calendar.MINUTE, minute) }
        calendar.set(Calendar.SECOND, 0)

        return calendar.timeInMillis
    }

    fun getNumberWithTwoCharacter(number: Int): String {
        return String.format("%02d", number)
    }

    /**
     * Ch???n th???i gian b???ng TimePickerDialog
     * @param context Context
     * @param time Long?
     * @param timePickerListener TimePickerListener
     */
    fun timePicker(context: Context?, time: Long?, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()

        if (time != null)
            calendar.timeInMillis = time

        timePicker(context, calendar, listener)
    }

    fun timePicker(context: Context?, time: String?, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()

        if (!time.isNullOrEmpty()) {
            val list = time.split(":")

            try {
                calendar.set(Calendar.HOUR_OF_DAY, list[0].toInt())
                calendar.set(Calendar.MINUTE, list[1].toInt())
                calendar.set(Calendar.SECOND, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        timePicker(context, calendar, listener)
    }

    fun timePicker(context: Context?, calendar: Calendar, listener: DateTimePickerListener) {
        context?.let {
            object : TimePicker(it, R.style.PickerTheme, calendar) {
                override fun onSelected(time: String, milliseconds: Long) {
                    listener.onSelected(time, milliseconds)
                }
            }.show()
        }
    }

    /**
     * Ch???n ng??y th??ng b???ng DatePickerDialog
     * @param context Context
     * @param dateVn String dd/MM/yyyy
     * @param listener DateTimePickerListener
     */
    fun dateTimePicker(context: Context?, dateTimeVn: String?, minDateTimeVn: String?, maxDateTimeVn: String?, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()

        val minDate = convertDateTimeVnToMillisecond(minDateTimeVn)
        val maxDate = convertDateTimeVnToMillisecond(maxDateTimeVn)

        convertDateTimeVnToMillisecond(dateTimeVn)?.let {
            calendar.timeInMillis = it
        }

        context?.let {
            object : DateTimePicker(it, R.style.PickerTheme, calendar, minDate, maxDate) {
                override fun onSelected(calendar: Calendar, date: String, milliseconds: Long) {
                    object : TimePicker(it, R.style.PickerTheme, calendar) {
                        override fun onSelected(time: String, milliseconds: Long) {
                            val dateTime = "$date $time"
                            listener.onSelected(dateTime, milliseconds)
                        }
                    }.show()
                }
            }.show()
        }
    }

    fun minDatePicker(context: Context?, dateVn: String?, minDateVn: String?, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()

        val minDate = convertDateVnToMillisecond(dateVn) ?: System.currentTimeMillis()

        convertDateVnToMillisecond(dateVn)?.let {
            calendar.timeInMillis = it
        }

        datePicker(context, calendar, minDate, null, listener)
    }

    fun minDatePicker(context: Context?, dateVn: String?, minDateVn: Long?, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()

        convertDateVnToMillisecond(dateVn)?.let {
            calendar.timeInMillis = it
        }

        datePicker(context, calendar, minDateVn, null, listener)
    }

    fun datePicker(context: Context?, dateVn: String?, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()

        convertDateVnToMillisecond(dateVn)?.let {
            calendar.timeInMillis = it
        }

        datePicker(context, calendar, null, null, listener)
    }

    fun datePicker(context: Context?, dateVn: String?, minDateVn: String?, maxDateVn: String?, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()

        val minDate = convertDateVnToMillisecond(minDateVn)
        val maxDate = convertDateVnToMillisecond(maxDateVn)

        convertDateVnToMillisecond(dateVn)?.let {
            calendar.timeInMillis = it
        }

        datePicker(context, calendar, minDate, maxDate, listener)
    }

    fun datePicker(context: Context?, dateVn: String?, minDateVn: Long?, maxDateVn: Long?, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()

        convertDateVnToMillisecond(dateVn)?.let {
            calendar.timeInMillis = it
        }

        datePicker(context, calendar, minDateVn, maxDateVn, listener)
    }

    fun datePicker(context: Context?, millisecond: Long?, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()

        millisecond?.let {
            calendar.timeInMillis = millisecond
        }

        datePicker(context, calendar, null, null, listener)
    }

    fun datePicker(context: Context?, year: Int, month: Int, day: Int, listener: DateTimePickerListener) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        datePicker(context, calendar, null, null, listener)
    }

    fun datePicker(context: Context?, calendar: Calendar, minDateVn: Long?, maxDateVn: Long?, listener: DateTimePickerListener) {
        context?.let {
            object : DatePicker(it, R.style.PickerTheme, calendar, minDateVn, maxDateVn) {
                override fun onSelected(date: String, milliseconds: Long) {
                    listener.onSelected(date, milliseconds)
                }
            }.show()
        }
    }

    fun getDayOfWeek(time: String?): String {
        if (time.isNullOrEmpty())
            return ""

        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormatSv.parse(time)
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                "TH??? 2"
            }
            Calendar.TUESDAY -> {
                "TH??? 3"
            }
            Calendar.WEDNESDAY -> {
                "TH??? 4"
            }
            Calendar.THURSDAY -> {
                "TH??? 5"
            }
            Calendar.FRIDAY -> {
                "TH??? 6"
            }
            Calendar.SATURDAY -> {
                "TH??? 7"
            }
            else -> {
                "CH??? NH???T"
            }
        }
    }

    fun getDayAndMonth(time: String?): String {
        if (time.isNullOrEmpty())
            return ""

        val calendar = Calendar.getInstance()
        calendar.time = simpleDateFormatSv.parse(time)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        return "$day TH??NG $month"
    }

    fun getDateTime(timeStamp: Long): String? {
        val date = Date(timeStamp)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return sdf.format(date)
    }
}