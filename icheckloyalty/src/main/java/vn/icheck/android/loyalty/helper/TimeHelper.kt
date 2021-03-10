package vn.icheck.android.loyalty.helper

import java.text.SimpleDateFormat
import java.util.*

object TimeHelper {

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
}