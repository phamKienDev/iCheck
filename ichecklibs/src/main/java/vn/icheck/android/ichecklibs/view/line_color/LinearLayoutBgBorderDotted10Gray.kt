package vn.icheck.android.ichecklibs.view.line_color

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper

class LinearLayoutBgBorderDotted10Gray : LinearLayout {
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
        background = GradientDrawable().also {
            it.setStroke(SizeHelper.size1,Constant.getLineColor(context), SizeHelper.size10.toFloat(), SizeHelper.size6.toFloat())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.setPadding(SizeHelper.size7, SizeHelper.size7, SizeHelper.size7, SizeHelper.size7)
            }
            it.cornerRadius= SizeHelper.size10.toFloat()
        }
    }
}