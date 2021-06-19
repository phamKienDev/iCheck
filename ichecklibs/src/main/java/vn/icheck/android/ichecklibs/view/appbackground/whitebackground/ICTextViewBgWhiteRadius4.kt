package vn.icheck.android.ichecklibs.view.appbackground.whitebackground

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import vn.icheck.android.ichecklibs.ViewHelper

class ICTextViewBgWhiteRadius4 : TextView {
    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setup()
    }

    private fun setup() {
        background=ViewHelper.bgWhiteCorners4(context)
    }
}