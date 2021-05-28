package vn.icheck.android.util.ick

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.*
import vn.icheck.android.helper.DialogHelper

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