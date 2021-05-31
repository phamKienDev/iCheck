package vn.icheck.android.ichecklibs.view.app_background.white_background

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.ichecklibs.ViewHelper

class ICTextViewBgWhiteUnpressGrayPress : AppCompatTextView {
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
        background=ViewHelper.bgWhitePress(context)
    }
}