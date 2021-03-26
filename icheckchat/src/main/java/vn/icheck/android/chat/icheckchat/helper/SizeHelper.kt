package vn.icheck.android.chat.icheckchat.helper

import android.content.res.Resources

internal object SizeHelper {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}