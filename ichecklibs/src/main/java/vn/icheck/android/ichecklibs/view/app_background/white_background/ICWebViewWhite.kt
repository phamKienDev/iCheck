package vn.icheck.android.ichecklibs.view.app_background.white_background

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import vn.icheck.android.ichecklibs.Constant

class ICWebViewWhite : WebView {
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
        setBackgroundColor(Constant.getAppBackgroundWhiteColor(context))
    }
}