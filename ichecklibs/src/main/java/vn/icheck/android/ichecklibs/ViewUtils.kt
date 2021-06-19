package vn.icheck.android.ichecklibs

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.media.ThumbnailUtils
import android.text.Editable
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.ichecklibs.databinding.CustomLayoutToastBinding
import vn.icheck.android.ichecklibs.util.AfterTextWatcher
import java.io.File

var toast: Toast? = null

fun Context.showToastError(msg: String? = null) {
    if (!msg.isNullOrEmpty()) {
        showCustomIconToast(msg, R.drawable.ic_waring_1_white_40dp)
    } else {
        showCustomIconToast(getString(R.string.error_default), R.drawable.ic_waring_1_white_40dp)
    }
}

@MainThread
fun Context.showCustomIconToast(msg: String, @DrawableRes id: Int) {
    val binding = CustomLayoutToastBinding.inflate(LayoutInflater.from(this))
    toast?.cancel()
    toast = Toast(this)
    binding.root.background=ViewHelper.bgPopupCorners4(this)
    binding.tvContent.text = msg
    binding.icCustom.setImageResource(id)
    toast?.view = binding.root
    toast?.setGravity(Gravity.CENTER, 0, 0)
    toast?.duration = Toast.LENGTH_SHORT
    toast?.show()
}

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
