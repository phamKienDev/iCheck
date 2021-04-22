package vn.icheck.android.helper

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.Constant

object TextColorHelper {
    fun getColorNormalText(context: Context): Int {
        return if (Constant.normalTextColor.isNotEmpty()) {
            Color.parseColor(Constant.normalTextColor)
        } else {
            ContextCompat.getColor(context, R.color.colorNormalText)
        }
    }
}