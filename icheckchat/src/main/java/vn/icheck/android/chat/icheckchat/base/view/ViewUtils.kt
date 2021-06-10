package vn.icheck.android.chat.icheckchat.base.view

import android.app.AlarmManager
import android.content.Context
import android.content.res.Resources
import android.media.MediaMetadataRetriever
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.databinding.CustomLayoutToastBinding
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

var toast: Toast? = null

const val USER_LEVEL_STANDARD = 1
const val USER_LEVEL_SILVER = 2
const val USER_LEVEL_GOLD = 3
const val USER_LEVEL_DIAMOND = 4

val defaultError = R.drawable.ic_default_image_upload_150_chat

fun setClickListener(clickListener: View.OnClickListener, vararg views: View) {
    for (view in views) {
        view.setOnClickListener(clickListener)
    }
}

fun setGoneView(vararg views: View) {
    for (view in views) {
        view.setGone()
    }
}

fun setVisibleView(vararg views: View) {
    for (view in views) {
        view.setVisible()
    }
}

fun Context.showToastError(msg: String? = null) {
    if (!msg.isNullOrEmpty()) {
        showCustomIconToast(msg, R.drawable.ic_waring_1_white_40dp_chat)
    } else {
        showCustomIconToast(getString(R.string.error_default), R.drawable.ic_waring_1_white_40dp_chat)
    }
}

fun Context.showToastSuccess(msg: String? = null) {
    if (!msg.isNullOrEmpty()) {
        showCustomIconToast(msg, R.drawable.ic_success_white_40dp_chat)
    } else {
        showCustomIconToast(getString(R.string.error_default), R.drawable.ic_waring_1_white_40dp_chat)
    }
}

fun Context.showConfirm(title: String?, message: String?, disagree: String?, agree: String?, isCancelable: Boolean, listener: ConfirmDialogListener) {
    object : ConfirmDialogChat(this, title, message, disagree, agree, isCancelable) {
        override fun onDisagree() {
            listener.onDisagree()
        }

        override fun onAgree() {
            listener.onAgree()
        }

        override fun onDismiss() {

        }
    }.show()
}

@MainThread
fun Context.showCustomIconToast(msg: String, @DrawableRes id: Int) {
    val binding = CustomLayoutToastBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    binding.tvContent.text = msg
    binding.icCustom.setImageResource(id)
    toast?.view = binding.root
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.show()
}

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun View.visibleOrGone(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.visibleOrInvisible(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

fun View.setAllEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup) this.children.forEach { child -> child.isEnabled = enabled }
}

fun loadImageUrlRounded(image: AppCompatImageView, url: String?, error: Int, roundCorners: Int) {
    if (url.isNullOrEmpty()) {
        Glide.with(image.context)
                .load(error)
                .transform(CenterCrop(), RoundedCorners(roundCorners))
                .into(image)
        return
    }


    Glide.with(image.context)
            .load(url)
            .placeholder(R.drawable.ic_default_image_upload_150_chat)
            .error(error)
            .transform(CenterCrop(), RoundedCorners(roundCorners))
            .into(image)
}

fun loadImageFileRounded(image: AppCompatImageView, file: File?, error: Int, roundCorners: Int) {
    if (file == null || !file.exists()) {
        Glide.with(image.context.applicationContext)
                .load(error)
                .transform(CenterCrop())
                .into(image)
        return
    }

    Glide.with(image.context.applicationContext)
            .load(file)
            .placeholder(R.drawable.ic_default_image_upload_150_chat)
            .error(error)
            .transform(CenterCrop(), RoundedCorners(roundCorners))
            .into(image)
}


fun loadImageUrl(image: CircleImageView, url: String?, error: Int, placeholder: Int) {
    if (url.isNullOrEmpty()) {
        Glide.with(image.context)
                .load(error)
                .placeholder(placeholder)
                .transform(CenterCrop())
                .into(image)
        return
    }

    Glide.with(image.context)
            .load(url)
            .placeholder(placeholder)
            .error(error)
            .transform(CenterCrop())
            .into(image)
}

fun loadImageUrl(image: AppCompatImageView, url: String?, error: Int) {
    if (url.isNullOrEmpty()) {
        Glide.with(image.context)
                .load(error)
                .transform(CenterCrop())
                .into(image)
        return
    }

    Glide.with(image.context)
            .load(url)
            .error(error)
            .transform(CenterCrop())
            .into(image)
}

fun loadImageUrl(image: AppCompatImageView, url: String?, error: Int, placeholder: Int) {
    if (url.isNullOrEmpty()) {
        Glide.with(image.context)
                .load(error)
                .placeholder(placeholder)
                .transform(CenterCrop())
                .into(image)
        return
    }

    Glide.with(ShareHelperChat.getApplicationByReflect())
            .load(url)
            .placeholder(placeholder)
            .error(error)
            .transform(CenterCrop())
            .into(image)
}

fun loadImageFileNotRounded(image: AppCompatImageView, file: File?) {
    if (file == null || !file.exists()) {
        Glide.with(image.context)
                .load(defaultError)
                .transform(CenterCrop())
                .into(image)
        return
    }

    Glide.with(image.context)
            .load(file)
            .placeholder(defaultError)
            .error(defaultError)
            .transform(CenterCrop())
            .into(image)
}

fun loadImageUrlNotCrop(image: AppCompatImageView, url: String?, error: Int) {
    if (url.isNullOrEmpty()) {
        Glide.with(image.context)
                .load(error)
                .transform(CenterCrop())
                .into(image)
        return
    }

    Glide.with(image.context)
            .load(url)
            .placeholder(defaultError)
            .error(error)
            .into(image)
}

fun AppCompatImageView.loadImageFromVideoFile(file: File?, time: Long? = null, corners: Int? = null, error: Int? = null) {
    if (file != null) {
        if (file.absolutePath.contains(".mp4")) {
            try {
                val inputStream = FileInputStream(file.absolutePath)
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(inputStream.fd)
                val bitmap = mediaMetadataRetriever.getFrameAtTime(time ?: 0)
                this.setImageBitmap(bitmap)

            } catch (e: Exception) {
                loadImageFileRounded(this, file, corners ?: dpToPx(4))
            }
        } else {
            loadImageFileRounded(this, file, corners ?: dpToPx(4))
        }
    } else {
        this.setImageResource(error ?: defaultError)
    }
}

fun loadImageFileRounded(image: AppCompatImageView, file: File?, roundCorners: Int) {
    if (file == null || !file.exists()) {
        Glide.with(image.context)
                .load(defaultError)
                .transform(CenterCrop())
                .into(image)
        return
    }

    Glide.with(image.context)
            .load(file)
            .placeholder(defaultError)
            .error(defaultError)
            .transform(CenterCrop(), RoundedCorners(roundCorners))
            .into(image)
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun formatMoney(value: Long?): String {
    return try {
        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = ','
        symbols.groupingSeparator = ','
        val format = DecimalFormat("###,###,###,###", symbols)
        format.format(value)
    } catch (e: Exception) {
        "0"
    }
}

fun AppCompatImageView.setRankUserBig(rank: Int?) {
    when (rank) {
        USER_LEVEL_SILVER -> {
            this.setImageResource(R.drawable.ic_avatar_silver_24dp_chat)
        }
        USER_LEVEL_GOLD -> {
            this.setImageResource(R.drawable.ic_avatar_gold_24dp_chat)
        }
        USER_LEVEL_DIAMOND -> {
            this.setImageResource(R.drawable.ic_avatar_diamond_24dp_chat)
        }
        USER_LEVEL_STANDARD -> {
            this.setImageResource(R.drawable.ic_avatar_standard_24dp_chat)
        }
        else -> {
            this.setImageResource(0)
        }
    }
}

fun convertMillisecondToDateVn(millisecond: Long?): String? {
    if (millisecond == null || millisecond == -1L)
        return null

    val sdf = SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault())

    return try {
        sdf.format(Date(millisecond))
    } catch (e: Exception) {
        null
    }
}

fun convertMillisecondToTimeVn(millisecond: Long?): String? {
    if (millisecond == null || millisecond == -1L)
        return null

    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    return try {
        sdf.format(Date(millisecond))
    } catch (e: Exception) {
        null
    }
}

fun convertDateTimeSvToTimeDateVn(millisecond: Long?): String? {
    if (millisecond == null || millisecond == -1L)
        return null

    val sdfVn = SimpleDateFormat("HH:mm, dd/MM/yyyy")
    sdfVn.timeZone = TimeZone.getTimeZone("GMT+07")

    return try {
        sdfVn.format(Date(millisecond))
    } catch (e: Exception) {
        null
    }
}

fun convertDateTimeSvToTimeVn(millisecond: Long?): String? {
    if (millisecond == null || millisecond == -1L)
        return null

    val sdfVn = SimpleDateFormat("HH:mm")
    sdfVn.timeZone = TimeZone.getTimeZone("GMT+07")

    return try {
        sdfVn.format(Date(millisecond))
    } catch (e: Exception) {
        null
    }
}

fun convertDateTimeSvToCurrentDay(millisecond: Long?): String {
    val time = System.currentTimeMillis().minus(millisecond ?: 0)

    return when {
        time <= intervalMinute -> {
            "Vừa xong"
        }
        time <= intervalHour -> {
            (time / intervalMinute).toString() + " phút trước"
        }
        time < AlarmManager.INTERVAL_DAY -> {
            if (soSanhCungNgay(millisecond)) {
                convertDateTimeSvToTimeVn(millisecond) ?: ""
            } else {
                convertDateTimeSvToTimeDateVn(millisecond) ?: ""
            }
        }
        else -> {
            convertDateTimeSvToTimeDateVn(millisecond) ?: ""
        }
    }
}

fun soSanhCungNgay(millisecond: Long?): Boolean {
    var mDate = Date()
    val sdf = SimpleDateFormat("dd/MM/yyyy")
    sdf.timeZone = TimeZone.getTimeZone("GMT+07")
    if (millisecond != null) {
        mDate = Date(millisecond)
    }
    val currenDay = sdf.format(Date(System.currentTimeMillis()))
    val customDay = sdf.format(mDate)

    return currenDay.equals(customDay)
}

fun chenhLechGio(time: Long?, newTime: Long?, hour: Int): Boolean {
    return if (time != null && newTime != null) {
        (newTime- time) > (hour * intervalHour)
    } else {
        true
    }
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

private const val intervalHour = 60 * 60 * 1000
private const val intervalMinute = 60 * 1000