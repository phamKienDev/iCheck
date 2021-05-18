package vn.icheck.android.ichecklibs

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
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

fun View.visibleOrGone(logic: Boolean) {
    if (logic) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun View.visibleOrInvisible(logic: Boolean) {
    if (logic) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}

fun Int.toPx(res: Resources): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), res.displayMetrics).toInt()
}

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun Float.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun Float.toPx(res: Resources): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, res.displayMetrics)
}

fun Int.spToPx(): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()
}

fun Float.spToPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)
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

fun Context.isSoftNavigationBarAvailable(): Boolean {
    val navBarInteractionModeId = resources.getIdentifier(
            "config_navBarInteractionMode",
            "integer",
            "android"
    )
    if (navBarInteractionModeId > 0 && resources.getInteger(navBarInteractionModeId) > 0) {
        // nav gesture is enabled in the settings
        return false
    }
    val appUsableScreenSize = Point()
    val realScreenSize = Point()
    val defaultDisplay = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    defaultDisplay.getSize(appUsableScreenSize)
    defaultDisplay.getRealSize(realScreenSize)
    return appUsableScreenSize.y < realScreenSize.y
}

fun Context.getNavigationHeight():Int {
    return if (isSoftNavigationBarAvailable()) {
        val resources: Resources = resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    } else {
        0
    }
}

fun View.setMarginConstraintLayout(left:Int, right:Int, top:Int, bottom:Int) {
    try {
        val lp = layoutParams as ConstraintLayout.LayoutParams
        lp.setMargins(left,top, right, bottom)
        requestLayout()
    } catch (e: Exception) {
    }

}