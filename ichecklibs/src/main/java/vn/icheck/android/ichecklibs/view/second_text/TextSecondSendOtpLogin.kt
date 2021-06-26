package vn.icheck.android.ichecklibs.view.second_text

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.ichecklibs.ColorManager

class TextSecondSendOtpLogin : AppCompatTextView {
    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    private fun setup() {
        val states = arrayOf(
                intArrayOf(-android.R.attr.state_enabled),// disabled
                intArrayOf(android.R.attr.state_enabled))// enabled)

        val colors = intArrayOf(
                ColorManager.getSecondTextColor(context),
                ColorManager.getSecondaryColor(context))

        val colorStateList = ColorStateList(states, colors)
        setTextColor(colorStateList)
        includeFontPadding = false
    }
}