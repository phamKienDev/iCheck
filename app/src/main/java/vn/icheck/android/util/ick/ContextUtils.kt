package vn.icheck.android.util.ick

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.*
import vn.icheck.android.constant.SIMPLE_ERROR_MESSAGE
import vn.icheck.android.databinding.ToastSimpleErrorBinding
import vn.icheck.android.databinding.ToastSimpleSuccessBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.util.kotlin.ToastUtils
import java.lang.Exception

infix fun Activity.showLoadingTimeOut(timeInMillis: Long) {
    GlobalScope.launch(Dispatchers.Main) {
        try {
            withTimeoutOrNull(timeInMillis) {
                DialogHelper.showLoading(this@showLoadingTimeOut)
            }
        } catch (e: Exception) {
            DialogHelper.closeLoading(this@showLoadingTimeOut)
        }
    }
}

infix fun Fragment.showLoadingTimeOut(timeInMillis: Long) {
    GlobalScope.launch(Dispatchers.Main) {
        if (isVisible)
            try {
                withTimeoutOrNull(timeInMillis) {
                    DialogHelper.showLoading(this@showLoadingTimeOut)
                }
            } catch (e: Exception) {
                DialogHelper.closeLoading(this@showLoadingTimeOut)
            }
    }
}

fun Activity.dismissLoadingScreen() {
    GlobalScope.launch(Dispatchers.Main) {
        DialogHelper.closeLoading(this@dismissLoadingScreen)
    }
}

fun Fragment.dismissLoadingScreen() {
    if (isVisible)
        GlobalScope.launch(Dispatchers.Main) {
            DialogHelper.closeLoading(this@dismissLoadingScreen)
        }
}

var toast: Toast? = null
infix fun Context.showShortError(message: String?) {
    if (message != null) {
        showSimpleErrorToast(message)
    } else {
        showSimpleErrorToast(SIMPLE_ERROR_MESSAGE)
    }
}

infix fun Context.showSimpleSuccessToast(msg: String?) {
    val binding = ToastSimpleSuccessBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    binding.tvContent.text = msg
    toast?.view = binding.root
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.show()
}

fun Context.showSimpleSuccessToast(icon: Int, msg: String?) {
    val binding = ToastSimpleSuccessBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    binding.imgIcon.setImageResource(icon)
    binding.tvContent.text = msg
    toast?.view = binding.root
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.show()
}

@MainThread
infix fun Context.showSimpleErrorToast(msg: String?) {
    val binding = ToastSimpleErrorBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    if (!msg.isNullOrEmpty()) {
        binding.tvContent.text = msg
    } else {
        binding.tvContent.text = "Đã xảy ra lỗi, vui lòng thử lại sau!"
    }
    toast?.view = binding.root
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.show()
}

@MainThread
infix fun Context.showSimpleErrorLongToast(msg: String?) {
    val binding = ToastSimpleErrorBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    if (!msg.isNullOrEmpty()) {
        binding.tvContent.text = msg
    } else {
        binding.tvContent.text = "Đã xảy ra lỗi, vui lòng thử lại sau!"
    }
    toast?.view = binding.root
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast?.duration = Toast.LENGTH_LONG
    toast?.show()
}

@MainThread
infix fun Context.showSimpleErrorToast(msg: Int) {
    val binding = ToastSimpleErrorBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    binding.tvContent.text = this.getString(msg)
    toast?.view = binding.root
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.show()
}

fun Context.showCustomIconToast(msg: String?, @DrawableRes id: Int) {
    val binding = ToastSimpleErrorBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    binding.tvContent.text = msg
    binding.icCustom.setImageResource(id)
    toast?.view = binding.root
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.show()
}

infix fun Context.getDrawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(this, id)

fun FragmentActivity.dismissKeyboard() {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Context.getDeviceWidth() = this.resources.displayMetrics.widthPixels

fun Context.getDeviceHeight() = this.resources.displayMetrics.heightPixels

fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}