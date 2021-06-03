package vn.icheck.android.ichecklibs.view.appbackground.white_background

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import vn.icheck.android.ichecklibs.ViewHelper

class ICConstraintLayoutBgWhiteRadius10 : ConstraintLayout {
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
        background=ViewHelper.bgWhiteCorners10(context)
    }
}