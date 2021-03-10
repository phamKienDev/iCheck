package vn.icheck.android.fragments.message

import vn.icheck.android.util.text.TimeHelper
import java.text.SimpleDateFormat
import java.util.*

class MsgTimeHelper(val timeStamp: Long):TimeHelper() {

    val todaySdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val otherSdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    fun getTime(): String {
        calendar.timeInMillis = timeStamp
        val date = calendar.time
        val differenceHours = getDifferenceHours(date, currentCalendar.time)
        if (differenceHours in 24..48) {
            return "Hôm qua"
        }
        if (differenceHours < 24) {
            return todaySdf.format(date)
        }
        return otherSdf.format(calendar.time)
    }
}