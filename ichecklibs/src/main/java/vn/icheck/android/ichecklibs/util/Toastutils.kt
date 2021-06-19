package vn.icheck.android.ichecklibs.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.databinding.ToastSimpleErrorBinding


var toast: Toast? = null

/*
* Success
* */
@MainThread
infix fun Context.showShortSuccessToast(message: String?) {
    showCustomToast(R.drawable.ic_success_white_40px, message ?: "", Toast.LENGTH_SHORT)
}

@MainThread
fun Context.showShortSuccessToast(@StringRes message: Int) {
    showCustomToast(R.drawable.ic_success_white_40px, getString(message), Toast.LENGTH_SHORT)
}

@MainThread
infix fun Context.showLongSuccessToast(message: String?) {
    showCustomToast(R.drawable.ic_success_white_40px, message ?: "", Toast.LENGTH_LONG)
}

@MainThread
infix fun Context.showLongSuccessToast(@StringRes message: Int) {
    showCustomToast(R.drawable.ic_success_white_40px, getString(message), Toast.LENGTH_LONG)
}

/*
* Error
* */
@MainThread
infix fun Context.showShortErrorToast(message: String?) {
    showCustomToast(R.drawable.ic_waring_white_40_px, if (!message.isNullOrEmpty()) {
        message
    } else {
        getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
    }, Toast.LENGTH_SHORT)
}

@MainThread
infix fun Context.showShortErrorToast(@StringRes msg: Int) {
    showCustomToast(R.drawable.ic_waring_white_40_px, getString(msg), Toast.LENGTH_SHORT)
}

@MainThread
infix fun Context.showLongErrorToast(message: String?) {
    showCustomToast(R.drawable.ic_waring_white_40_px, if (!message.isNullOrEmpty()) {
        message
    } else {
        getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
    }, Toast.LENGTH_LONG)
}

@MainThread
infix fun Context.showLongErrorToast(@StringRes msg: Int) {
    showCustomToast(R.drawable.ic_waring_white_40_px, getString(msg), Toast.LENGTH_LONG)
}

@MainThread
fun Context.showDurationErrorToast( msg: String,duration: Int) {
    showCustomToast(R.drawable.ic_waring_white_40_px, msg, duration)
}

/*
* Base
* */
fun Context.showCustomToast(@DrawableRes id: Int, msg: String, duration: Int) {
    toast?.cancel()
    toast = Toast(this)

    val binding = ToastSimpleErrorBinding.inflate(LayoutInflater.from(this))
    binding.tvMessage.text = msg
    binding.imgIcon.setImageResource(id)

    toast?.view = binding.root
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast?.duration = duration
    toast?.show()
}