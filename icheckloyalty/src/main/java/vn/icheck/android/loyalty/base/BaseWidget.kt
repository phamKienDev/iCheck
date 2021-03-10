package vn.icheck.android.loyalty.base

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.toast_error.view.*
import vn.icheck.android.loyalty.R
import java.text.SimpleDateFormat
import java.util.*


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
    if (isVisible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

fun View.visibleOrInvisible(isVisible: Boolean) {
    if (isVisible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.INVISIBLE
    }
}

const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val HOURS_MINUTES ="HH:mm dd/MM/yyyy"

fun String.getHHMMDate():String {
    val sdf = SimpleDateFormat(ISO_FORMAT, Locale.getDefault())
    val df = SimpleDateFormat(HOURS_MINUTES, Locale.getDefault())
    val date = sdf.parse(this)
    return df.format(date ?: Calendar.getInstance())
}

fun View.hideKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun showCustomErrorToast(context: Context, message: String) {
    val toast = Toast(context)
    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
    toast.duration = Toast.LENGTH_SHORT
    val lf = LayoutInflater.from(context)
    val v = lf.inflate(R.layout.toast_error, null)
    v.tv_message.text = message
    toast.view = v
    toast.show()
}