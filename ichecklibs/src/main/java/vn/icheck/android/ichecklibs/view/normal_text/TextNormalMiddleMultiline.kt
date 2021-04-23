package vn.icheck.android.ichecklibs.view.normal_text

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.MiddleMultilineTextView
import vn.icheck.android.ichecklibs.R

class TextNormalMiddleMultiline:MiddleMultilineTextView {

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
        setTextColor(if (Constant.normalTextColor.isNotEmpty()) {
            Color.parseColor(Constant.normalTextColor)
        } else {
            ContextCompat.getColor(context, R.color.colorNormalText)
        })
        includeFontPadding = false
    }
}