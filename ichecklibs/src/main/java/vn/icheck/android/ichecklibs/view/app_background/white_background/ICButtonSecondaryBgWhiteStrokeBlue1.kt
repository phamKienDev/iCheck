package vn.icheck.android.ichecklibs.view.app_background.white_background

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.view.secondary.ButtonSecondary
import vn.icheck.android.ichecklibs.view.secondary.TextSecondary

class ICButtonSecondaryBgWhiteStrokeBlue1: ButtonSecondary {
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
        background=ViewHelper.bgWhiteRadius8StrokeBlue1(context)
        setBackgroundColor(Constant.getAppBackgroundWhiteColor(context))
    }
}