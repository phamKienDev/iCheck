package vn.icheck.android.ichecklibs.view.accent.accent_red

import android.content.Context
import android.util.AttributeSet
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.view.TextHeader

open class TextHeaderAccentRed : TextHeader {

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
        setTextColor(Constant.getAccentRedColor(context))
    }
}