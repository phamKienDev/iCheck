package vn.icheck.android.ichecklibs.view.appbackground.whitebackground

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import vn.icheck.android.ichecklibs.ViewHelper

class ICNestedScrollViewBgWhiteRadiusTop16 : NestedScrollView {
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
        background=ViewHelper.bgWhiteCornersTop16(context)
    }
}