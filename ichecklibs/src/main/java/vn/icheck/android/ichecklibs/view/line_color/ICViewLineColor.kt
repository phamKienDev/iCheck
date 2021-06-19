package vn.icheck.android.ichecklibs.view.line_color

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper

class ICViewLineColor : View {
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
        setBackgroundColor(Constant.getLineColor(context))
    }
}