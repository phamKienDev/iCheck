package vn.icheck.android.ichecklibs.view.second_text

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.view.TextSubheader2

class TextSecondSubheader2 : TextSubheader2 {
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
        setTextColor(Constant.getSecondTextColor(context))
        includeFontPadding = false
    }
}