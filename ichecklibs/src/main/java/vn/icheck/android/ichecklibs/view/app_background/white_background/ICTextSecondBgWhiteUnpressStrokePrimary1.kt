package vn.icheck.android.ichecklibs.view.app_background.white_background

import android.content.Context
import android.util.AttributeSet
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.view.second_text.TextSecondStateEnable

class ICTextSecondBgWhiteUnpressStrokePrimary1:TextSecondStateEnable {
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
        background= ViewHelper.bgWhitePressStrokePrimary1Radius4(context)
    }
}