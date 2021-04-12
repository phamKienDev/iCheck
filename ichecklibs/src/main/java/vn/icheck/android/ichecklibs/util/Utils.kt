package vn.icheck.android.ichecklibs

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.core.view.children
import vn.icheck.android.ichecklibs.databinding.ToastSimpleErrorBinding
import vn.icheck.android.ichecklibs.databinding.ToastSimpleSuccessBinding


fun Context.getDeviceWidth() = this.resources.displayMetrics.widthPixels

fun Context.getDeviceHeight() = this.resources.displayMetrics.heightPixels

fun View?.beGone() {
    this?.visibility = View.GONE
}

fun View.beVisible() {
    this.visibility = View.VISIBLE
}

fun View.beInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.visibleOrInvisible(logic: Boolean) {
    if (logic) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}

fun Int.toPx(res: Resources): Int {
//    return this * (res.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), res.displayMetrics).toInt()
}

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun Float.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}


fun Float.toPx(res: Resources): Float {
//    return this * (res.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), res.displayMetrics)
}


infix fun Context.showSimpleSuccessToast(msg: String?) {
    val binding = ToastSimpleSuccessBinding.inflate(LayoutInflater.from(this))
    var toast: Toast? = null
    toast?.cancel()
    toast = Toast(this)
    binding.tvContent.text = msg
    toast.view = binding.root
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.duration = Toast.LENGTH_SHORT
    toast.show()
}

fun Context.showSimpleSuccessToast(icon: Int, msg: String?) {
    var toast: Toast? = null
    val binding = ToastSimpleSuccessBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    binding.imgIcon.setImageResource(icon)
    binding.tvContent.text = msg
    toast.view = binding.root
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.duration = Toast.LENGTH_SHORT
    toast.show()
}

@MainThread
infix fun Context.showSimpleErrorToast(msg: String?) {
    var toast: Toast? = null
    val binding = ToastSimpleErrorBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    if (!msg.isNullOrEmpty()) {
        binding.tvContent.text = msg
    } else {
        binding.tvContent.text = "Đã xảy ra lỗi, vui lòng thử lại sau!"
    }
    toast.view = binding.root
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.duration = Toast.LENGTH_SHORT
    toast.show()
}

@MainThread
infix fun Context.showSimpleErrorLongToast(msg: String?) {
    var toast: Toast? = null
    val binding = ToastSimpleErrorBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    if (!msg.isNullOrEmpty()) {
        binding.tvContent.text = msg
    } else {
        binding.tvContent.text = "Đã xảy ra lỗi, vui lòng thử lại sau!"
    }
    toast.view = binding.root
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.duration = Toast.LENGTH_LONG
    toast.show()
}

@MainThread
infix fun Context.showSimpleErrorToast(msg: Int) {
    var toast: Toast? = null
    val binding = ToastSimpleErrorBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    binding.tvContent.text = this.getString(msg)
    toast.view = binding.root
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.duration = Toast.LENGTH_SHORT
    toast.show()
}

fun View.setAllEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup) this.children.forEach { child -> child.isEnabled = enabled }
}