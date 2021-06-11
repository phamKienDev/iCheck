package vn.icheck.android.util.text

import vn.icheck.android.network.models.ICProductReviews
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CommentTimeUtil(val comments: ICProductReviews.Comments) : TimeHelper() {

    fun getTime(): String {
        val date = sdf.parse(comments.createAt)
        if (date != null) {
            gmtVn(date)
            calendar.time = date

            val diff = getDifferenceDays(date, currentCalendar.time)
            val differenceHours = getDifferenceHours(date, currentCalendar.time)
            if (differenceHours > 24 && diff < 7) {
                return "$diff ngày trước"
            } else if (differenceHours <= 24) {
                val differMinutes = getDifferenceMinutes(date, currentCalendar.time)
                return if (differenceHours >= 1) {
                    "$differenceHours giờ trước"
                } else if (differMinutes >= 1) {
                    "${getDifferenceMinutes(date, currentCalendar.time)} phút trước"
                } else {
                    "${getDifferenceSeconds(date, currentCalendar.time)} giây trước"
                }
            }
        }
        return show.format(calendar.time)
    }

}

class TestTimeUtil(val timeS: String) : TimeHelper() {

    fun getTime(): String {
        val date = sdf.parse(timeS)

        if (date != null) {
            gmtVn(date)
            calendar.time = date
            val diff = getDifferenceDays(date, currentCalendar.time)
            val differenceHours = getDifferenceHours(date, currentCalendar.time)
            if (differenceHours > 24 && diff < 2) {
                return "$diff ngày trước"
            } else if (diff > 2){
                return vn.icheck.android.helper.TimeHelper.convertDateTimeSvToTimeDateVnPhay(timeS).toString()
            } else if (differenceHours <= 24) {
                val differMinutes = getDifferenceMinutes(date, currentCalendar.time)
                return if (differenceHours >= 1) {
                    "$differenceHours giờ trước"
                } else if (differMinutes >= 1) {
                    "${getDifferenceMinutes(date, currentCalendar.time)} phút trước"
                } else {
                    "${getDifferenceSeconds(date, currentCalendar.time)} giây trước"
                }
            }
        }
        return showHistory.format(calendar.time)
    }

    fun getTimeDate(): String {
        val date = sdf.parse(timeS)

        if (date != null) {
            gmtVn(date)
            calendar.time = date
            val diff = getDifferenceDays(date, currentCalendar.time)
            val differenceHours = getDifferenceHours(date, currentCalendar.time)
            if (differenceHours > 24 && diff < 2) {
                return "$diff ngày trước"
            } else if (diff > 2){
                return vn.icheck.android.helper.TimeHelper.convertDateSvToDateVn(timeS).toString()
            } else if (differenceHours <= 24) {
                val differMinutes = getDifferenceMinutes(date, currentCalendar.time)
                return when {
                    differenceHours >= 1 -> {
                        "$differenceHours giờ trước"
                    }
                    differMinutes >= 1 -> {
                        "${getDifferenceMinutes(date, currentCalendar.time)} phút trước"
                    }
                    else -> {
                        "${getDifferenceSeconds(date, currentCalendar.time)} giây trước"
                    }
                }
            }
        }
        return showHistory.format(calendar.time)
    }

    fun getTimeDateNews(): String {
        val date = sdf.parse(timeS)

        if (date != null) {
            gmtVn(date)
            calendar.time = date
            val diff = getDifferenceDays(date, currentCalendar.time)
            val differenceHours = getDifferenceHours(date, currentCalendar.time)
            if (differenceHours > 24 && diff < 2) {
                return "$diff ngày trước"
            } else if (diff > 2){
                return vn.icheck.android.helper.TimeHelper.convertDateTimeSvToTimeDateVnV2(timeS).toString()
            } else if (differenceHours <= 24) {
                val differMinutes = getDifferenceMinutes(date, currentCalendar.time)
                return when {
                    differenceHours >= 1 -> {
                        "$differenceHours giờ trước"
                    }
                    differMinutes >= 1 -> {
                        "${getDifferenceMinutes(date, currentCalendar.time)} phút trước"
                    }
                    else -> {
                        "Vừa xong"
                    }
                }
            }
        }
        return showHistory.format(calendar.time)
    }
}


class MessageTimeUtil(val timestamp: Long) : TimeHelper() {

    fun getTime(): String {
        val date = Date(timestamp)
        calendar.time = date
//        gmtVn(date)
        val diff = getDifferenceDays(date, currentCalendar.time)
        val differenceHours = getDifferenceHours(date, currentCalendar.time)
        if (differenceHours > 24 && diff < 7) {
            return "$diff ngày trước"
        } else if (differenceHours <= 24) {
            val differMinutes = getDifferenceMinutes(date, currentCalendar.time)
            return if (differenceHours >= 1) {
                "$differenceHours giờ trước"
            } else if (differMinutes >= 1) {
                "${getDifferenceMinutes(date, currentCalendar.time)} phút trước"
            } else if (getDifferenceSeconds(date, currentCalendar.time) > 0) {
                "${getDifferenceSeconds(date, currentCalendar.time)} giây trước"
            } else {
                "Đã gửi"
            }
        }
        return show.format(calendar.time)
    }
}

class ReviewsTimeUtils(val reviewsRow: ICProductReviews.ReviewsRow) : TimeHelper() {

    fun getTime(): String {
        val date = sdf.parse(reviewsRow.createAt)
        if (date != null) {
            gmtVn(date)
            calendar.time = date

            val diff = getDifferenceDays(date, currentCalendar.time)
            val differenceHours = getDifferenceHours(date, currentCalendar.time)
            if (differenceHours > 24 && diff < 7) {
                return "$diff ngày trước"
            } else if (differenceHours <= 24) {
                val differMinutes = getDifferenceMinutes(date, currentCalendar.time)
                return if (differenceHours >= 1) {
                    "$differenceHours giờ trước"
                } else if (differMinutes >= 1) {
                    "${getDifferenceMinutes(date, currentCalendar.time)} phút trước"
                } else {
                    "${getDifferenceSeconds(date, currentCalendar.time)} giây trước"
                }
            }
        }
        return show.format(calendar.time)
    }
}

class CreatedAtTimeHelper(val createdAt: String) : TimeHelper() {
    fun getTime(): String {
        val date = sdf.parse(createdAt)
        if (date != null) {
            gmtVn(date)
            calendar.time = date
            val diff = getDifferenceDays(date, currentCalendar.time)
            val differenceHours = getDifferenceHours(date, currentCalendar.time)
            if (differenceHours > 24 && diff < 7) {
                return "$diff ngày trước"
            } else if (differenceHours <= 24) {
                val differMinutes = getDifferenceMinutes(date, currentCalendar.time)
                return if (differenceHours >= 1) {
                    "$differenceHours giờ trước"
                } else if (differMinutes >= 1) {
                    "${getDifferenceMinutes(date, currentCalendar.time)} phút trước"
                } else {
                    "${getDifferenceSeconds(date, currentCalendar.time)} giây trước"
                }
            }
        }
        return show.format(calendar.time)
    }
}

open class TimeHelper {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val show = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val showHistory = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val currentCalendar = Calendar.getInstance()

    fun gmtVn(date: Date) {
        date.time += 7 * 3600 * 1000
    }

    fun getDifferenceDays(date_1: Date, date_2: Date): Long {
        val diff = date_2.time - date_1.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun getDifferenceHours(date_1: Date, date_2: Date): Long {
        val diff = date_2.time - date_1.time
        return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun getDifferenceMinutes(date_1: Date, date_2: Date): Long {
        val diff = date_2.time - date_1.time
        return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun getDifferenceSeconds(date_1: Date, date_2: Date): Long {
        val diff = date_2.time - date_1.time
        return TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS)
    }
}