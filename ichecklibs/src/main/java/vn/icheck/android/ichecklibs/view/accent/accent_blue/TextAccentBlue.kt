package vn.icheck.android.ichecklibs.view.accent.accent_blue

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.ichecklibs.Constant

open class TextAccentBlue : AppCompatTextView {
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
        setTextColor(Constant.getAccentBlueColor(context))
        includeFontPadding = false
    }
}