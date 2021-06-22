package vn.icheck.android.ichecklibs.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.text.Editable
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children

fun Float.toPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
}

fun Int.toPx(): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()
}

fun EditText.addPriceTextWatcher() {
    this.addTextChangedListener(object : AfterTextWatcher() {
        var current = ""

        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                if (current.length <= s.toString().length) {
                    this@addPriceTextWatcher.removeTextChangedListener(this)
                    val cleanString = s.toString().replace("[,.đ]".toRegex(), "")
                    val formatted = String.format("%dđ", cleanString.toLong())
                    current = formatted
                    this@addPriceTextWatcher.setText(formatted)
                    this@addPriceTextWatcher.setSelection(formatted.length)
                    this@addPriceTextWatcher.addTextChangedListener(this)
                } else {
                    this@addPriceTextWatcher.removeTextChangedListener(this)
                    val cleanString = s.toString().replace("[,.đ]".toRegex(), "")
                    if (cleanString.length > 1) {
                        val formatted = String.format("%dđ", cleanString.substring(0, cleanString.length - 1).toLong())
                        current = formatted
                        this@addPriceTextWatcher.setText(formatted)
                        this@addPriceTextWatcher.setSelection(formatted.length)
                    } else {
                        this@addPriceTextWatcher.setText("")
                        current = ""
                    }
                    this@addPriceTextWatcher.addTextChangedListener(this)
                }
            }
        }
    })
}


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

//TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, ICheckApplication.getInstance().resources.displayMetrics)

fun Int.spToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
}

fun Float.spToPx(): Float {
    return this * Resources.getSystem().displayMetrics.scaledDensity
}

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun Float.dpToPx(): Float {
    return this * Resources.getSystem().displayMetrics.density
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

