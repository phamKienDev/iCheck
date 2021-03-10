package vn.icheck.android.util.ick

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Patterns
import android.view.View
import androidx.annotation.IntRange
import androidx.core.app.ShareCompat
import kotlinx.android.synthetic.main.item_item_reward_v2.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.screen.user.webview.WebViewActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

infix fun String?.getDateFormat(format: String): String {
    return if (this != null) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = sdf.parse(this)
        val show = SimpleDateFormat(format, Locale.getDefault())
        if (date != null) {
            show.format(date)
        } else {
            "Đang cập nhật"
        }
    } else {
        "Đang cập nhật"
    }
}

fun String.getHtmlText(): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
} else {
    Html.fromHtml(this)
}

fun String?.getInfo() = if (this?.replace("null", "")?.trim().isNullOrEmpty()) "Đang cập nhật" else this


fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String?.getImageSize(width: Int = 0, height: Int = 0, @IntRange(from = 0, to = 100) quality: Int = 100): String? {
    var url = this
    val query = arrayListOf<String?>()
    if (width > 0) {
        query.add("width=$width")
    }
    if (height > 0) {
        query.add("height=$height")
    }
    if (quality in 1..100) {
        query.add("quality=$quality")
    }
    if (query.isNotEmpty()) {
        url += "?${query.joinToString(separator = "&")}"
    }
    return url
}

/**
 * "list-image-resize": [
"150x150",
"300x300",
"720x720",
"900x900",
"1536x1536",
"100x100",
"200x200",
"400x400",
"600x600",
"1024x1024"
],
 */
fun String?.getImageBySize(width: Int = 0, height: Int = 0): String? {
    val arrUrl = this?.split(".")

    var url =  arrUrl?.subList(0,3)?.joinToString(separator = ".")
    val query = arrayListOf<String?>()
    if (width > 0) {
        query.add("-$width")
    }
    if (height > 0) {
        query.add("$height")
    }
    if (query.isNotEmpty()) {
        url += query.joinToString(separator = "-")
    }
    url+=".${arrUrl?.last()}"
    return url
}

fun String.getTime(format: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val resultSdf = SimpleDateFormat(format, Locale.getDefault())
        var result = this
        val date = sdf.parse(this)
        gmtVn(date)
        date?.let {
            result = resultSdf.format(it)
        }
        result
    } catch (e: Exception) {
        ""
    }
}

fun Long.getTime(format: String, formatVn:Boolean = true): String {
    return try {
        val resultSdf = SimpleDateFormat(format, Locale.getDefault())
        var result = ""
        val date = Date(this)
        if (formatVn) {
            gmtVn(date)
        }
        date.let {
            result = resultSdf.format(it)
        }
        result
    } catch (e: Exception) {
        ""
    }
}

fun String.getHourMinutesTime(): String {
    return getTime("HH:mm, dd/MM/yyyy")
}

fun String.getDayTime(): String {
    return getTime("dd/MM/yyyy")
}

fun String.getBirthDay() = getTime("dd-MM-yyyy")

fun String.getNotifyTime() :String{
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = sdf.parse(this) ?: Calendar.getInstance().time
        gmtVn(date)
        val currentDate = Calendar.getInstance().time
        val diffHours = getDifferenceHours(date, currentDate)
        val diffMinutes = getDifferenceMinutes(date, currentDate)
        when {
            diffMinutes <= 1 -> {
                "Vừa xong"
            }
            diffMinutes <= 60 -> {
                "$diffMinutes phút trước"
            }
            diffHours <= 24 -> {
                "$diffHours giờ trước"
            }
            else -> {
                getTime("HH:mm, dd/MM/yyyy")
            }
        }
    } catch (e: Exception) {
        "Vừa xong"
    }

}

fun Long.getNotifyTimeVn() :String{
    return try {
        val date = Date(this)
        val currentDate = Calendar.getInstance().time
        val diffHours = getDifferenceHours(date, currentDate)
        val diffMinutes = getDifferenceMinutes(date, currentDate)
        when {
            diffMinutes <= 1 -> {
                "Vừa xong"
            }
            diffMinutes <= 60 -> {
                "$diffMinutes phút trước"
            }
            diffHours <= 24 -> {
                "$diffHours giờ trước"
            }
            else -> {
                getTime("HH:mm, dd/MM/yyyy", false)
            }
        }
    } catch (e: Exception) {
        "Vừa xong"
    }

}

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



fun String.convertBirthDay(format: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val resultSdf = SimpleDateFormat(format, Locale.getDefault())
    var result = this
    val date = sdf.parse(this)
    date?.let {
        result = resultSdf.format(it)
    }
    return result
}

fun String.startCallPhone() {
    ICheckApplication.currentActivity()?.let {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel: $this")
        it.startActivity(intent)
    }
}

fun String.startSentEmail(subject: String? = null, content: String? = null, chooserTitle: String? = null) {
    ICheckApplication.currentActivity()?.let {
        ShareCompat.IntentBuilder.from(it)
                .setType("message/rfc822")
                .addEmailTo(this)
                .setSubject(subject)
                .setText(content)
                //.setHtmlText(body) //If you are using HTML in your body text
                .setChooserTitle(chooserTitle)
                .startChooser()
    };
}

fun String.startWebView() {
    ICheckApplication.currentActivity()?.let {
        WebViewActivity.start(it, this)
    }
}

fun Long.getMoneyFormat() = String.format("%,dđ", this).replace(".", ",")

fun Int.getMoneyFormat() = String.format("%,dđ", this).replace(".", ",")

fun String.getRewardType() :String{
   return when (this) {
        "PRODUCT_SHIP" -> {
             "Giao tận nơi"
        }
        "PRODUCT_IN_SHOP" -> {
            "Nhận tại cửa hàng"
        }
        "CARD" -> {
            "Quà thẻ cào"
        }
        "LUCKY" -> {
             "Quà tinh thần"
        }
        else ->{
             "Quà hiện vật"
        }
    }
}

fun String.isPhoneNumber():Boolean {
    return if (this.length == 10 && this.startsWith("0") && !this.startsWith("00")) {
        true
    }else this.length == 11 && this.startsWith("84")
}

fun String.createShareAction(activity:Activity, title:String) {
    ShareCompat.IntentBuilder.from(activity)
            .setType("text/plain")
            .setChooserTitle(title)
            .setText(this)
            .startChooser()
}

fun String.createShareAction(activity:Activity) {
    ShareCompat.IntentBuilder.from(activity)
            .setType("text/plain")
            .setText(this)
            .startChooser()
}

fun Any?.toStringNotNull() :String{
    return this.toString().replace("null","")
}
